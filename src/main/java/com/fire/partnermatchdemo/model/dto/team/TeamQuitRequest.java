package com.fire.partnermatchdemo.model.dto.team;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {


    private static final long serialVersionUID = 463766095204345026L;

    /**
     * teamId
     */
    private Long teamId;


}
