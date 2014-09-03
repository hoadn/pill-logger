/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.echo.holographlibrary;

import android.graphics.Path;
import android.graphics.Region;

import java.util.ArrayList;
import java.util.List;

public class StackBar {
    private String mName;
    private List<StackBarSection> mSections = new ArrayList<StackBarSection>();

    public List<StackBarSection> getSections() {
        return mSections;
    }

    public void setSections(List<StackBarSection> sections) {
        this.mSections = sections;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getTotalValue(){
        int value = 0;

        for(StackBarSection section : mSections){
            value += section.getTargetValue();
        }

        return value;
    }
}
