

package com.mickstarify.fortunemod;

/**
 * Copyright Michael Johnston
 * Created by michael on 4/12/14.
 */
public class Quote {
    public int id;
    public String quote;
    public String category;
    public boolean isOffensive;

    public Quote (int id, String quote, String category, boolean isOffensive){
        this.id = id;
        this.quote = quote;
        this.category = category;
        this.isOffensive = isOffensive;
    }

}
