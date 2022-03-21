package fr.insee.eno.utils;

import fr.insee.eno.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class FileUtils {

    final static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Find the inputStream from the given existing resource in parameter. The given resource
     * must be localised in the classpath. The localisation in the classpath is made by the classloader of
     * the current thread.
     * @param classpathResource : the relative path for the given resource in the classpath. Must not be null
     * @return an optional with an inputstream to the given resource. Otherwise (resource not found, access forbiden, ...)
     * an empty optional
     */
    public static Optional<InputStream> openInputStream(@Nonnull String classpathResource){
        return findTUsingClassLoader(ClassLoader::getResourceAsStream, Objects.requireNonNull(classpathResource));
    }

    /**
     * Find the URL from the given resource in parameter. The given resource
     * must be localised in the classpath. The localisation in the classpath is made by the classloader of
     * the current thread.
     * @param classpathResource : the relative path for the given resource in the classpath. Must not be null
     * @return an optional with an URL to the given resource. Otherwise (resource not found, access forbiden, ...)
     * an empty optional
     */
    public static Optional<URL> findURL(@Nonnull String classpathResource) {
        return findTUsingClassLoader(ClassLoader::getResource, Objects.requireNonNull(classpathResource));
    }

    private static <T> Optional<T> findTUsingClassLoader(BiFunction<ClassLoader, String, T> search, String path){
        T resolved=search.apply(Thread.currentThread().getContextClassLoader(), path);
        if (resolved==null){
            logger.warn("Unable to find ressource "+path+ " in class path");
        }
        return Optional.ofNullable(resolved);
    }

    /**
     * ### Ugly method ###
     *
     * Open a new outputstream to a resource localised in the classpath tree. The target classpath resource
     * is not supposed to exist but its parent does. So the method creates the resource in the classpath and
     * opens an outputstream.
     * @param classpathResource : the path to the target resource relative to classpath. It must be non null. it must
     *                          be a '/'-separated path (with at least one '/')
     * @return an outputstream to the the target classpath resource
     * @throws IOException : if the parent of the target classpath resource doesn't exist or if it is impossible
     * to write in th classpath
     */
    public static OutputStream newOutputStream(@Nonnull String classpathResource) throws IOException {
        Optional<URL> absoluteURLToParent=findURL(
                getParentInClasspathResource(classpathResource)
        );
        if (absoluteURLToParent.isEmpty() ){
            throw new IOException(getParentInClasspathResource(classpathResource) +" doesn't exist in classpath");
        }

        try {
            return Files.newOutputStream(Path.of(absoluteURLToParent.get().toURI()).resolve(Path.of(classpathResource).getFileName()));
        } catch (URISyntaxException e) {
            throw new IOException(getParentInClasspathResource(classpathResource) +" is resolved against "+ absoluteURLToParent.get()+" in classpath, which is not correct");
        }
    }

    private static String getParentInClasspathResource(String classpathResource) {
        return Path.of(classpathResource)
                .getParent()
                .toString()
                .replace("\\", "/"); /* Don't use windows any more in order tu suppress this line */
    }

    /**
     * Find the File object from the given resource in parameter. The given resource
     * must be localised in the classpath. The localisation in the classpath is made by the classloader of
     * the current thread.
     * @param classpathResource : the relative path for the given resource in the classpath. Must not be null
     * @return an optional with a File wich represents the given resource. Otherwise (resource not found, access forbiden, ...)
     * an empty optional
     */
    public static Optional<File> findFile(String classpathResource) {
        return findURL(classpathResource).map(url-> {
            try {
                return url.toURI();
            } catch (URISyntaxException e) {
                return null;
            }
        }).map(File::new);
    }

    public static Path subTempFolder(String survey) {
        return Constants.TEMP_FOLDER.resolve(survey);
    }

    public static Path tempNullTmp (Path folder){
        return folder.resolve("null.tmp");
    }

    public static Path tempDDIFolder(Path tempFolder){
        return tempFolder.resolve("/ddi");
    }

    public static File tempMappingTmp (Path subTempFolder){
        return subTempFolder.resolve("mapping.xml").toFile();
    }

    /**
     * Create an outputStream creating or opening the target file specified by path. If the parent
     * directory of the target file does not exists, it  creates it.
     * @param path : the path to the target file. Must be not null
     * @return an output stream to the target file
     */
    public static OutputStream newOutputStreamCreatingParentDirectory(@Nonnull Path path) throws IOException {
        Path parentDirectory = Objects.requireNonNull(path).getParent();
        if (! Files.exists(parentDirectory)){
            Files.createDirectories(parentDirectory);
        }
        return Files.newOutputStream(path);
    }
}
