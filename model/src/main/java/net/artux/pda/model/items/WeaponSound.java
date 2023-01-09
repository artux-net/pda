package net.artux.pda.model.items;

import java.io.Serializable;

import lombok.Data;

@Data
public class WeaponSound implements Serializable {

    private String shot;
    private String reload;

}
