package com.tgn;

import javax.xml.parsers.*;
import org.w3c.dom.*;


/*
http://www.developer.com/java/other/article.php/3665131/An-Improved-Approach-for-Creating-SVGXML-Code-and-SVGXML-DOM-Nodes-using-Java.htm
*/

class SvgGraphics{
    //----------------------------------------------------//
  
    /*
      The purpose of this method is to create a general node
      having any name, and any number of attributes with any 
      attribute names and any String values for the 
      attributes, or no attributes at all.
   
      The first parameter is a reference to the document to
      which the new node belongs.
  
      The second parameter is a reference to the parent node
      to which this node is to be appended so as to become a
      child of that node. If this parameter is null, the new
      node is appended to the document.  Otherwise, it is
      appended to the specified parent node.
   
      The third parameter is a String that specifies the type
      of node.
  
      The fourth parameter to the method must be a reference
      to an array object of type String.  This array must 
      contain an even number of elements.  Each pair of 
      elements constitutes the name and the value of an 
      attribute, in the order name, value, name, value, etc.
  
      An example of the recommended usage of the method
      follows:
      Element abc = SvgGraphics.makeNode(
      document,
      def,//parent could be null
      "ghi",//node type
      new String[]{"name","value",
      "name","value",
      "name","value",
      });//end makeNode method
    */
    static Element makeNode(Document document,
			    Element parent,
			    String nodeType,
			    String[] data){
  
	Element element = 
	    (Element)document.createElement(nodeType);
    
	if(parent == null){
	    //For the special case of parent equal to null,
	    // append the new node to the document.
	    document.appendChild(element);
	}else{
	    //Otherwise, append the new node to the specified
	    // parent.
	    parent.appendChild(element);
	}//end else
  
	//Deal with elements that have no attributes.
	if(data == null){
	    return element;
	}//end if
    
	for(int cnt=0;cnt<data.length;cnt+=2){
	    String name = data[cnt];
	    String value = data[cnt+1];
	    element.setAttribute(name,value);
	}//end for loop
    
	return element;
    }//end makeNode
    //----------------------------------------------------//

    //This is a utility method that is used to execute code
    // that is the same regardless of the graphic image
    // being produced. This method is not new to this
    // program.
    static Document getDocument(){
	Document document = null;
	try{
	    DocumentBuilderFactory factory = 
		DocumentBuilderFactory.newInstance();

	    DocumentBuilder builder = 
		factory.newDocumentBuilder();
	    document = builder.newDocument();
	    document.setXmlStandalone(false);
	}catch(Exception e){
	    e.printStackTrace(System.err);
	    System.exit(0);
	}//end catch
	return document;
    }//end getDocument
    //----------------------------------------------------//
    
  
}//end class SvgGraphics
