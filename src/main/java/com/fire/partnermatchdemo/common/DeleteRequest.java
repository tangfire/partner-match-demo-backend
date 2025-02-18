package com.fire.partnermatchdemo.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -7416886719498424820L;

    private long id;
}
