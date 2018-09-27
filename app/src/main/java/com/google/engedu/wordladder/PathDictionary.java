/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordladder;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

public class PathDictionary {
    private static final int MAX_WORD_LENGTH = 4;
    static HashSet<String> words = new HashSet<>();
    private static HashMap<String, GraphNode> wordNodeMap=new HashMap<>();
    private static ArrayList<GraphNode> wordNodes=new ArrayList<>();

    public PathDictionary(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        Log.i("Word ladder", "Loading dict");
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        Log.i("Word ladder", "Loading dict");
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() > MAX_WORD_LENGTH) {
                continue;
            }
            words.add(word);
            wordNodeMap.put(word, new GraphNode(word));
            wordNodes.add(wordNodeMap.get(word));
        }

        for(int i=0;i<words.size()-1;i++) {
            GraphNode tempNode1=wordNodes.get(i);
            String x=tempNode1.word;
            for(int j=i+1;j<words.size();j++) {
                GraphNode tempNode2=wordNodes.get(j);
                String y=tempNode2.word;
                if(x.length()==y.length()) {
                    int count=0;
                    for(int k=0;k<x.length();k++) {
                        if(x.charAt(k)!=y.charAt(k))
                            ++count;
                    }
                    //add to neighbours if qualifies
                    if(count==1) {
                        tempNode1.neighbours.add(tempNode2);
                        tempNode2.neighbours.add(tempNode1);
                    }
                }
            }
        }
    }

    public boolean isWord(String word) {
        return words.contains(word.toLowerCase());
    }

    private ArrayList<String> neighbours(String word) {
        ArrayList<String> toReturn=new ArrayList<>();
        for(GraphNode x: wordNodeMap.get(word).neighbours)
            toReturn.add(x.word);
        return toReturn;
    }

    public String[] findPath(String start, String end) {
        HashMap<GraphNode, GraphNode> childParentStart=new HashMap<>(), childParentEnd=new HashMap<>();
        Queue<GraphNode> startQueue=new ArrayDeque<>(), endQueue=new ArrayDeque<>();
        ArrayList<String> path=new ArrayList<>();
        GraphNode tempNode1=wordNodeMap.get(start), tempNode2=wordNodeMap.get(end);
        //adding immediate neighbors to map and queues
        tempNode1.visited=tempNode2.visitedByOther=true;
        //startQueue.addAll(tempNode1.neighbours);
        //endQueue.addAll(tempNode2.neighbours);
        for(GraphNode x: tempNode1.neighbours) {
            childParentStart.put(x, tempNode1);
            startQueue.add(x);
            x.addedToQueue1=true;
        }
        for(GraphNode x: tempNode2.neighbours) {
            childParentEnd.put(x, tempNode2);
            endQueue.add(x);
            x.addedToQueue2=true;
        }
        //start traversing
        while (!startQueue.isEmpty()&&!endQueue.isEmpty()) {
            tempNode1=startQueue.poll();
            tempNode2=endQueue.poll();
            Log.e("the words :", tempNode1.word+" "+tempNode2.word);
            if(tempNode1.visitedByOther||tempNode2.visited) {
                GraphNode tempNode=(tempNode1.visitedByOther)?tempNode1:tempNode2;
                Log.e("common :", "word is "+ tempNode.word);
                path.add(tempNode.word);
                tempNode2=childParentEnd.get(tempNode);
                tempNode1=childParentStart.get(tempNode);
                while (tempNode1!=wordNodeMap.get(start)) {
                    path.add(0, tempNode1.word);
                    tempNode1=childParentStart.get(tempNode1);
                }
                while (tempNode2!=wordNodeMap.get(end)) {
                    path.add(tempNode2.word);
                    tempNode2=childParentEnd.get(tempNode2);
                }
                break;
            }
            for(GraphNode x: tempNode1.neighbours)
                if(!x.visited&&!x.addedToQueue1) {
                    //Log.e("adding :", "to startQueue "+x.word);
                    startQueue.add(x);
                    x.addedToQueue1=true;
                    childParentStart.put(x, tempNode1);
                }
            for(GraphNode x: tempNode2.neighbours)
                if(!x.visitedByOther&&!x.addedToQueue2) {
                    //Log.e("adding :", "to endQueue "+x.word);
                    endQueue.add(x);
                    x.addedToQueue2=true;
                    childParentEnd.put(x, tempNode2);
                }
            /*for(int i=0, j=0;i<tempNode1.neighbours.size()-1||j<tempNode2.neighbours.size()-1;) {
                GraphNode x1=tempNode1.neighbours.get(i);
                GraphNode x2=tempNode2.neighbours.get(j);
                if(!x1.visited) {
                    Log.e("adding :", "to startQueue "+x1.word);
                    startQueue.add(x1);
                    childParentStart.put(x1, tempNode1);
                }
                if(!x2.visited) {
                    Log.e("adding :", "to endQueue "+x2.word);
                    endQueue.add(x2);
                    childParentEnd.put(x2, tempNode2);
                }
                if(i<tempNode1.neighbours.size()-1)
                    i++;
                if(j<tempNode2.neighbours.size()-1)
                    j++;
            }*/
            tempNode1.visited=tempNode2.visitedByOther=true;
        }
        for(String x: path)
            Log.e("in path :", ""+x);

        String[] retPath=new String[path.size()];
        return (path.size()==0)?null:path.toArray(retPath);
    }

    private static class GraphNode {
        ArrayList<GraphNode> neighbours;
        boolean visited, visitedByOther, addedToQueue1, addedToQueue2;
        String word;
        GraphNode(String w) {
            neighbours=new ArrayList<>();
            visited=visitedByOther=addedToQueue1=addedToQueue2=false;
            word=w;
        }
    }
}
