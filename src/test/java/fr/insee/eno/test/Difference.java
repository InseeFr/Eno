package fr.insee.eno.test ; 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

public class Difference {

	public static void main(String[] args) {

		String baseFolder = "src/main/resources/";
		String fileToComparePath = baseFolder + "form.xhtml";
		String referenceFilePath = baseFolder + "cible.xhtml";
		String diffFilePath = baseFolder + "diff.txt";
		
		if(args.length>0){
			diffFilePath =  args[0];
		}else{
			System.out.println("Difference file path is missing");
		}
		
		if(args.length>1){
			fileToComparePath = args[1];
		}else{
			System.out.println("File path to compare is missing");
		}	
		if(args.length>2){
			referenceFilePath = args[2];
		}else{
			System.out.println("File path reference is missing");
		}
		
		BufferedReader referenceFile = null;
		BufferedReader fileToCompare = null;

		try {
			referenceFile = new BufferedReader(
					new FileReader(referenceFilePath));
		} catch (FileNotFoundException e) {
			System.out.println("File : " + referenceFilePath + " not Found");
			e.printStackTrace();
		}
		try {
			fileToCompare = new BufferedReader(
					new FileReader(fileToComparePath));
		} catch (FileNotFoundException e) {
			System.out.println("File : " + fileToComparePath + " not Found");
		}

		if (referenceFile != null & fileToCompare != null) {
			String referenceString = null;
			String referenceLine;
			try {
				while ((referenceLine = referenceFile.readLine()) != null) {
					referenceString = referenceString + referenceLine.trim();

				}
			} catch (IOException e1) {
				System.out.println("Impossible to read file : " + referenceFilePath);
				e1.printStackTrace();
			}

			String stringToCompare = null;
			String lineToCompare;
			try {
				while ((lineToCompare = fileToCompare.readLine()) != null) {
					stringToCompare = stringToCompare + lineToCompare.trim();
				}
			} catch (IOException e1) {
				System.out.println("Impossible to read file : " + fileToComparePath);
				e1.printStackTrace();
			}
			String diff = null;
			int indexOfDiff = 0;
			if (stringToCompare != null & referenceString != null) {
				diff = StringUtils.difference(referenceString,stringToCompare);
				indexOfDiff =  StringUtils.indexOfDifference(referenceString,stringToCompare);
				System.out.println("Comparison done");
			}
			if(indexOfDiff>0){
				System.out.println("Test failed");
				System.out.println("Index at which the file begins to differ : " +indexOfDiff);
				
			}else{
				System.out.println("Test success");
			}
			try (FileWriter fw = new FileWriter(diffFilePath, false);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
					if(indexOfDiff>0){
						out.println("Index at which the file begins to differ :" +indexOfDiff);
					}
					out.println(diff);
					System.out.println("Difference written in file : "+diffFilePath);
					
			} catch (IOException e) {
				System.out.println("Impossible to write file : " + diffFilePath);
				e.printStackTrace();
			}
			
			
		}

	}

}
