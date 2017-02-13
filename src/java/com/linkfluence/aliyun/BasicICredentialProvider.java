package com.linkfluence.aliyun;

import com.aliyuncs.auth.Credential;
import com.aliyuncs.auth.ICredentialProvider;

public class BasicICredentialProvider implements ICredentialProvider{
    private Credential credential = null;

    public BasicICredentialProvider(Credential credential){
      this.credential = credential;
    }

    public BasicICredentialProvider(String keyId, String secret){
      this.credential = new Credential(keyId,secret);
    }

    public Credential fresh(){
      return this.credential;
    }
}
