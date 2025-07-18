package com.abnormality.abnormalityaccept.config;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MinIoBucketPolicy {
    private String Version="2012-10-17";
    private List<Statement> Statement=new ArrayList<>();


    public static MinIoBucketPolicy readOnly(String bucketName){
        MinIoBucketPolicy policy=new MinIoBucketPolicy();
        Statement statement=new Statement();
//        statement.addAction("GetBucketLocation");
//        statement.addAction("ListBucket");
        statement.addAction("GetObject");
        statement.addResource(bucketName+"/*");
        statement.setEffect(true);
        policy.Statement.add(statement);
        return policy;
    }

    @Override
    public String toString(){
        String statementStr= Statement.toString();
        return "{\"Version\":\""+Version+"\",\"Statement\":"+statementStr+"}";
    }


}


class Statement{
    private List<String> Action=new ArrayList<>();
    private String Effect="Allow";
    private String Principal="*";
    private List<String> Resource=new ArrayList<>();

    public void addResource(String resource){
    	Resource.add("arn:aws:s3:::"+resource);
    }

    public void addAction(String action) {
        Action.add("s3:"+action);
    }
    public void setEffect(boolean isAllow){
        if(isAllow){
            Effect="Allow";
        }else{
            Effect="Deny";
        }
    }
    @Override
    public String toString(){
        String actionStr=JSONUtil.toJsonStr(Action);
        String resourceStr=JSONUtil.toJsonStr(Resource);
        return "{\"Action\":"+actionStr+",\"Effect\":\""+Effect+"\",\"Principal\":\""+Principal+"\",\"Resource\":"+resourceStr+"}";
    }


    public static void main(String[] args) {
    	MinIoBucketPolicy policy=MinIoBucketPolicy.readOnly("test");
    	System.out.println(policy);
	}
}


