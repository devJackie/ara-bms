package com.kthcorp.daisy.bms.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Data
@ToString
@EqualsAndHashCode
public class BmsUser {

    private Long id;

    private String userName;

    protected BmsUser() {}
}
