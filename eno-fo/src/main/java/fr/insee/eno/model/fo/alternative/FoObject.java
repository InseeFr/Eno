package fr.insee.eno.model.fo.alternative;

import fr.insee.eno.factory.FOUtils;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class FoObject {

    private Element element;

    public FoObject(Document document){
        element = document.createElement("fo:"+ FOUtils.camelToKebab(this.getClass().getSimpleName()));
    }

    public FoObject(Document document, Map<String, String> attributes){
        element = document.createElement("fo:"+ FOUtils.camelToKebab(this.getClass().getSimpleName()));
        if(attributes !=null) attributes.forEach((k,v)->element.setAttribute(k,v));
    }

    public void addChild(FoObject childFo){
        element.appendChild(childFo.getElement());
    }
    public void addChildren(FoObject... childrenFo){
        Arrays.stream(childrenFo).forEach(this::addChild);
    }

    public void addChildren(List<FoObject> childList){
        childList.stream().forEach(this::addChild);
    }
}
