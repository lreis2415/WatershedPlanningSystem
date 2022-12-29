package com.egc.demo;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "alternative")
public class Alternative {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int userGroupId;
    private int optimizationId;
    private int scenarioId;

}
