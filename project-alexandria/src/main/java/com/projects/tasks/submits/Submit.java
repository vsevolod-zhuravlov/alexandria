package com.projects.tasks.submits;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class Submit {
    private String id;

    private String fullName;
    private String address;

    private boolean isSubmitted;
    private boolean isConfirmed;
    private List<Confirmation> confirmations;

    private String text;
    private List<String> linksToFiles;

    public void addConfirmation(int minConfirmations, Confirmation confirmation) {
        confirmations.add(confirmation);
        boolean allConfirmed = true;
        int i = 0;
        for (Confirmation c : confirmations) {
            if (c.getStatus().equals(Confirmation.Status.DECLINED)) {
                allConfirmed = false;
                break;
            }
            i++;
        }
        if (allConfirmed && i >= minConfirmations) {
            setConfirmed(true);
        }
    }

    public boolean isConfirmedBy(String address) {
        for (Confirmation c : confirmations) {
            if (c.getConfirmerAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }
}
