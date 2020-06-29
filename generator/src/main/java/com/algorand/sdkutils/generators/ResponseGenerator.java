package com.algorand.sdkutils.generators;

import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Subscriber;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.beust.jcommander.Strings;

import java.util.ArrayList;
import java.util.Arrays;

public class ResponseGenerator extends Subscriber {

    public ResponseGenerator(Publisher publisher) {
        publisher.subscribeAll(this);
    }

    @Override
    public void terminate() {
        System.out.println("TERMINATE");
    }

    @Override
    public void onEvent(Publisher.Events event) {
        System.out.println("(event) - " + event);
    }

    @Override
    public void onEvent(Publisher.Events event, String[] notes) {
        System.out.println("(event, []notes) - " + String.join(",", notes));
    }

    @Override
    public void onEvent(Publisher.Events event, TypeDef type) {
        System.out.println("(event, TypeDef) - " + type.toString());
    }

    @Override
    public void onEvent(Publisher.Events event, StructDef sDef) {
        System.out.println("(event, TypeDef) - " + sDef.toString());
    }
}
