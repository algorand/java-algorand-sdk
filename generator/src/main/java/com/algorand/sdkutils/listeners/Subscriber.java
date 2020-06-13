package com.algorand.sdkutils.listeners;

import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;

public abstract class Subscriber {
    
    public abstract void terminate();
    
    public abstract void onEvent(Events event);
    
    /**
     * Used for:
     * 
     * @param event
     * @param note
     */
    public abstract void onEvent(Events event, String [] notes);
    
    /**
     * Used for: 
     * NEW_MODEL_PROPERTY: add property for a class from NEW_MODEL 
     * @param event
     * @param type
     */
    public abstract void onEvent(Events event, TypeDef type);
    
    /**
     * Used for:
     * NEW_MODEL: new model class 
     * @param event
     * @param sDef
     */
    public abstract void onEvent(Events event, StructDef sDef);
}
 
