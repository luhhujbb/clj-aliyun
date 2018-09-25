package com.linkfluence.aliyun;

import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.BasicCredentials;

public class BasicICredentialProvider implements AlibabaCloudCredentialsProvider{
    private AlibabaCloudCredentials credential = null;

    public BasicICredentialProvider(AlibabaCloudCredentials credential){
      this.credential = credential;
    }

    public BasicICredentialProvider(String keyId, String secret){
      this.credential = new BasicCredentials(keyId,secret);
    }

    public AlibabaCloudCredentials getCredentials(){
      return this.credential;
    }
}
