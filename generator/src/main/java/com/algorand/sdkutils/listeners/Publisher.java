package com.algorand.sdkutils.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.QueryDef;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;

public class Publisher {
    
    public enum Events {
        ALL,

        // Define a model object.
        NEW_MODEL,
        NEW_PROPERTY,
        END_MODEL,

        // register a model reference as a return type.
        REGISTER_RETURN_TYPE,

        // Define a query
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

    public void subscribeAll(Subscriber sub) {
        Stream.of(Events.values()).forEach(e -> subscribe(e, sub));
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

    public void publish(Events event, QueryDef query) {
        if (subscribers.get(event) != null) {
            for (Subscriber subscriber : subscribers.get(event)) {
                subscriber.onEvent(event, query);
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
