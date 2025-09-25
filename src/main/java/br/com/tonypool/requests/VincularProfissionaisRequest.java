package br.com.tonypool.requests;

import java.util.List;

public class VincularProfissionaisRequest {
    private List<Integer> profissionaisIds;

    public List<Integer> getProfissionaisIds() { return profissionaisIds; }
    public void setProfissionaisIds(List<Integer> profissionaisIds) { this.profissionaisIds = profissionaisIds; }
}
