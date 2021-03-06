package com.akura.parser.models;

import com.akura.parser.config.Config;
import com.akura.utility.OntologyWriter;

import org.apache.jena.ontology.*;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing an Ontology in Json to OWL context.
 */
public class Ontology {

    public static OntModel m = null;

    public HashMap<String, ArrayList<OntProperty>> classRegistry;

    /**
     * Method used to save the ontology file.
     *
     * @param m - ontology model.
     */
    public static void saveOntologyFile(OntModel m) {
        OntologyWriter.writeOntology(m, "test-1.owl");
    }

    public Ontology(OntModel m) {
        this.m = m;
        classRegistry = new HashMap<>();
        this.populateClasses();
    }

    /**
     * Method used to populate the classes.
     *
     * @return - Map.
     */
    public Map populateClasses() {
        ExtendedIterator<OntClass> classIter = this.m.listClasses();

        while (classIter.hasNext()) {
            OntClass clazz = classIter.next();
            if (clazz != null) {
                ExtendedIterator<? extends OntProperty> propertyIter = clazz.listDeclaredProperties();

                while (propertyIter.hasNext()) {
                    OntProperty property = propertyIter.next();

                    if (property != null) {
                        if (classRegistry.get(clazz.getURI()) != null) {
                            classRegistry.get(clazz.getURI()).add(property);

                        } else {
                            ArrayList arr = new ArrayList();
                            arr.add(property);
                            classRegistry.put(clazz.getURI(), arr);
                        }
                    }
                }
            }
        }
        return classRegistry;
    }


    /**
     * Method used to get the class name.
     *
     * @param entityName              - entity name.
     * @param literalProperties       - literal properties of the entity.
     * @param complexProperties       - complex properties of the entity.
     * @param simpleComplexProperties - simple complex properties of the entity.
     * @return - class name.
     */
    public String getClassName(String entityName, Map literalProperties, Map complexProperties, Map simpleComplexProperties) {

        String className = null;

        for (Object key : classRegistry.keySet()) {

            if (isTwoArrayListsWithSameValues(classRegistry.get(key.toString()),
                    this.mergeMaps(literalProperties, complexProperties, simpleComplexProperties))) {

                className = key.toString();

            }
        }

        if (className != null) {
            return className;
        } else {

            className = createNewClass(entityName, literalProperties, complexProperties, simpleComplexProperties).getURI();
        }

        return className;
    }

    /**
     * Method used to create a new class.
     *
     * @param className               - class name.
     * @param literalProperties       - literal properties of the class.
     * @param complexProperties       - complex properties of the class.
     * @param simpleComplexProperties - simple complex properties of the class.
     * @return - Ontology class.
     */
    public OntClass createNewClass(String className, Map literalProperties, Map complexProperties, Map simpleComplexProperties) {

        System.out.println("create class requested for: " + className);
        OntClass clazz = this.m.createClass(Config.ONTOLOGY_URI + className.replace("#", "").toUpperCase());
        classRegistry.put(clazz.getURI(), new ArrayList());


        // set simple properties
        for (Object key : literalProperties.keySet()) {
            OntProperty property = this.m.createDatatypeProperty(Config.ONTOLOGY_PROP_URI + key.toString().replace("#", "").toUpperCase());
            property.addDomain(clazz);
            classRegistry.get(clazz.getURI()).add(property);
        }

        for (Object key : simpleComplexProperties.keySet()) {
            OntProperty property = this.m.createDatatypeProperty(Config.ONTOLOGY_PROP_URI + key.toString().replace("#", "").toUpperCase());
            property.addDomain(clazz);
            classRegistry.get(clazz.getURI()).add(property);
        }

        // set complex properties
        for (Object key : complexProperties.keySet()) {

            OntProperty property = this.m.createObjectProperty(Config.ONTOLOGY_PROP_URI + key.toString().replace("#", "").toUpperCase());
            property.addDomain(clazz);
            ArrayList<Entity> arr = (ArrayList) complexProperties.get(key.toString());
            property.addRange(this.m.getOntClass(arr.get(0).classURI));
            classRegistry.get(clazz.getURI()).add(property);
        }

        return clazz;
    }

    /**
     * Method used to get the properties of the entity.
     *
     * @param propertyName - name of the property.
     * @return - Ontology property.
     */
    public OntProperty getProperty(String propertyName) {
        OntProperty selectedProperty = null;

        ExtendedIterator<? extends OntProperty> propertyIter = Ontology.this.m.listAllOntProperties();
        while (propertyIter.hasNext()) {
            OntProperty property = propertyIter.next();

            if (property != null && property.getLocalName().toUpperCase().equals(Config.ONTOLOGY_PROP_URI_PREFIX + propertyName.toUpperCase())) {
                selectedProperty = property;
            }
        }

        if (selectedProperty == null) {
            selectedProperty = this.m.createDatatypeProperty(Config.ONTOLOGY_PROP_URI + propertyName.replace("#", "").toUpperCase());
        }
        return selectedProperty;
    }

    /**
     * Method used to merge maps.
     *
     * @param literalProperties       - literal properties of an entity.
     * @param complexProperties       - complex properties of an entity.
     * @param simpleComplexProperties - simple complex properties of an entity.
     * @return - list fo merged maps.
     */
    public ArrayList mergeMaps(Map literalProperties, Map complexProperties, Map simpleComplexProperties) {
        ArrayList arr = new ArrayList();

        for (Object key : literalProperties.keySet()) {
            arr.add(key.toString());
        }

        for (Object key : complexProperties.keySet()) {
            arr.add(key.toString());
        }

        for (Object key : simpleComplexProperties.keySet()) {
            arr.add(key.toString());
        }

        return arr;
    }

    /**
     * Method used to check whether two arrays are with same values.
     *
     * @param classProperties  - class properties.
     * @param targetProperties - target properties.
     * @return - boolean value.
     */
    public boolean isTwoArrayListsWithSameValues(ArrayList<OntProperty> classProperties, ArrayList<Object> targetProperties) {

        if (classProperties == null && targetProperties == null)
            return true;

        if ((classProperties == null && targetProperties != null) || (classProperties != null && targetProperties == null))
            return false;


        for (Object targetProp : targetProperties) {
            boolean bool = false;


            for (OntProperty classProp : classProperties) {

                if (classProp.getLocalName().toString().toUpperCase().equals((Config.ONTOLOGY_PROP_URI_PREFIX + targetProp.toString()).toUpperCase()))
                    bool = true;
            }

            if (!bool) {

                return false;
            }
        }

        return true;
    }
}
