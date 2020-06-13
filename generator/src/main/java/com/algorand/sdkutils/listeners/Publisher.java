package com.algorand.sdkutils.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;

public class Publisher {
    
    public enum Events {
        ALL,
        NEW_MODEL,
        NEW_PROPERTY,
        NEW_RETURN_TYPE,
        NEW_QUERY,
        QUERY_PARAMETER,
        PATH_PARAMETER,
        BODY_CONTENT,
        END_QUERY, 
        
    }

    private HashMap<Events, ArrayList<Subscriber>> subscribers;
    private HashSet <Subscriber> all;
    
    public Publisher() {
        this.all = new HashSet<Subscriber>();
        this.subscribers = new HashMap<Events, ArrayList<Subscriber>>();
    }
    
    public void terminate() {
        for (Subscriber subscriber : all) {
            subscriber.terminate();
        }
    }
    
    public void subscribe(Events event, Subscriber subscriber) {
        if (subscribers.get(event) == null) {
            subscribers.put(event, new ArrayList<Subscriber>());
        }
        subscribers.get(event).add(subscriber);
        all.add(subscriber);
        
    }
    
    public void publish(Events event) {
        if (subscribers.get(event) != null) {
            for (Subscriber subscriber : subscribers.get(event)) {
                subscriber.onEvent(event);
            }
        }
    }
    
    public void publish(Events event, String [] notes) {
        if (subscribers.get(event) != null) {
            for (Subscriber subscriber : subscribers.get(event)) {
                subscriber.onEvent(event, notes);
            }
        }
    }
   
    public void publish(Events event, StructDef sDef) {
        if (subscribers.get(event) != null) {
            for (Subscriber subscriber : subscribers.get(event)) {
                subscriber.onEvent(event, sDef);
            }
        }
    }
    
    public void publish(Events event, TypeDef typedef) {
        if (subscribers.get(event) != null) {
            for (Subscriber subscriber : subscribers.get(event)) {
                subscriber.onEvent(event, typedef);
            }
        }
    }
}
