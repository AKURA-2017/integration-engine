package com.akura.integration.models;

import com.akura.utility.HashGeneratorClass;
import com.akura.integration.OntologyClass;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;

public class Feature {

    public static String prefix = "FEATURE";

    public Individual instance;
    public OntClass entityClass;

    private Property name;
    private ObjectProperty featureProperty;

    private String hash;
    private OntModel model;

    public Boolean status;

    public Feature(OntModel m) {
        this.model = m;
        this.entityClass = (OntClass) this.model.getOntClass(OntologyClass.FEATURE);
        this.initProperties();
    }

    public Feature(OntModel m, String name, String parentHash) {
        this.model = m;
        this.entityClass = (OntClass) this.model.getOntClass(OntologyClass.FEATURE);
        this.initProperties();

        this.hash = parentHash + "-" + HashGeneratorClass.generateHashForString(name, this.prefix);

        Individual ind = this.search(this.hash);
        if (ind == null) {
            this.instance = entityClass.createIndividual(OntologyClass.URI_NAMESPACE
                    + this.hash);
            this.status = false;
        } else {
            this.status = true;
            this.instance = ind;
        }
    }


    private void initProperties() {
        name = model.getProperty(OntologyClass.URI_NAMESPACE + "FeatureName");
        featureProperty = model.getObjectProperty(OntologyClass.URI_NAMESPACE + "FeatureProperty");

    }

    public void setName(String name) {
        this.instance.addProperty(this.name, name);
    }


    public void setProperty(String key, String value) {

        PropertyObject prop = new PropertyObject(this.model, key, this.hash);

        if (!prop.status) {
            prop.setKey(key);
            prop.setValue(value);
        }

        if (!this.model.listStatements(this.instance, this.featureProperty, prop.instance).hasNext()) {
            RelationshipGenerator.setRelationship(this.featureProperty, this.instance, prop.instance);
        }

    }

    public Individual search(String hash) {
        Individual ind = this.model.getIndividual(OntologyClass.URI_NAMESPACE + hash);
        return ind;
    }
}