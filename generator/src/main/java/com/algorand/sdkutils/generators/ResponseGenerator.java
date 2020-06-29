package com.algorand.sdkutils.generators;

import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Subscriber;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResponseGenerator extends Subscriber {

    public ResponseGenerator(Publisher publisher) {
        publisher.subscribeAll(this);
    }

    @Override
    public void terminate() {
        //System.out.println("TERMINATE");

    }

    @Override
    public void onEvent(Publisher.Events event) {
        System.out.println("(event) - " + event);
    }

    @Override
    public void onEvent(Publisher.Events event, String[] notes) {
        System.out.println("(event, []notes) - " + String.join(",", notes));
    }

    HashMap<StructDef, List<TypeDef>> responses = new HashMap<>();
    HashMap<StructDef, List<TypeDef>> models = new HashMap<>();
    List<TypeDef> activeList = null;
    //StructDef activeDef;

    @Override
    public void onEvent(Publisher.Events event, TypeDef type) {
        if (event == Publisher.Events.NEW_PROPERTY) {
            activeList.add(type);
            //System.out.println("(event, TypeDef) - " + type.toString());
        } else {
            System.out.println(event);
        }
    }

    @Override
    public void onEvent(Publisher.Events event, StructDef sDef) {
        activeList = new ArrayList<>();
        if (event == Publisher.Events.NEW_MODEL) {
            models.put(sDef, activeList);
            System.out.println("(event, TypeDef) - " + sDef.toString());
        } else {
            responses.put(sDef, activeList);
            System.out.println(event);
        }
    }
}
