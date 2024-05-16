package com.TradeFinance.accountservice.account;

import com.TradeFinance.accountservice.applicant.Applicant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity{
    @Id
    @Column(name="applicant_id")
    private Long applicantId;

    private String cifId;

    @Column(name="account_number")
    private Long accountNumber;

    @Column(name="account_type")
    private String accountType;

    @Column(name="branch_address")
    private String branchAddress;

    @OneToOne
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

}

