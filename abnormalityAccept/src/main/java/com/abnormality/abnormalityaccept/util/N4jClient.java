package com.abnormality.abnormalityaccept.util;

import com.abnormality.abnormalityaccept.dto.response.kg.KgData;
import com.abnormality.abnormalityaccept.dto.response.kg.KgEdge;
import com.abnormality.abnormalityaccept.dto.response.kg.KgNode;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;

public class N4jClient {

    private String url;
    private String username;
    private String password;

    private int maxNum=50;
    Driver driver=null;
    Session session=null;
    public N4jClient(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
        session = driver.session();
    }


    public String getAllRelations(List<String> entities,KgData kgData){

        EagerResult result=driver.executableQuery("""
            MATCH (n:Node)-[r]->(m:Node)
            WHERE n.name IN $entities
            RETURN n.name AS subject, type(r) AS predicate, m.name AS object
            UNION
            MATCH (n:Node)-[r]->(m:Node)
            WHERE m.name IN $entities
            RETURN m.name AS subject, type(r) AS predicate, n.name AS object
            """)
                .withParameters(Map.of("entities",entities))
                .execute();
        List<Record> records=result.records();
        kgData.setEdges(new ArrayList<>());
        kgData.setNodes(new ArrayList<>());
        Map<String,Integer> nodeSeen=new HashMap<>();
        Map<String,Integer> edgeSeen=new HashMap<>();
        int curNum=0;
        String relationStr="";
        for (Record record : records) {
            System.out.println(record.get("subject")+" "+record.get("predicate")+" "+record.get("object"));
            String subject=record.get(0).asString();
            String predicate=record.get(1).asString();
            String object=record.get(2).asString();
            String relation=subject+"-"+predicate+"->"+object;
            if(edgeSeen.getOrDefault(relation,0)!=1&&curNum<maxNum){
                edgeSeen.put(relation,1);
                kgData.getEdges().add(KgEdge.newRelation(subject,predicate,object));
            }
            if(nodeSeen.getOrDefault(subject,0)!=1&&curNum<maxNum){
                nodeSeen.put(subject,1);
                kgData.getNodes().add(KgNode.newNode(subject));
            }
            if(nodeSeen.getOrDefault(object,0)!=1&&curNum<maxNum){
                nodeSeen.put(object,1);
                kgData.getNodes().add(KgNode.newNode(object));
            }
            curNum++;
            relationStr+=relation+"\n";
        }
        return relationStr;
    }



}
