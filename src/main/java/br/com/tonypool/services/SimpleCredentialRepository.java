package br.com.tonypool.services;

import com.warrenstrange.googleauth.ICredentialRepository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

public class SimpleCredentialRepository implements ICredentialRepository {
    private final Map<String, String> credentials = new ConcurrentHashMap<>();

    @Override
    public String getSecretKey(String userName) {
        return credentials.get(userName);
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        credentials.put(userName, secretKey);
    }
}
