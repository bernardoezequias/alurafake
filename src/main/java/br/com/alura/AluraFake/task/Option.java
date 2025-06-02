package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Embeddable

public class Option {

    @NotBlank
    @Size(min = 4, max = 80)
    @Column(name = "`option`")
    @JsonProperty("option")
    private String option;
    @JsonProperty("isCorrect")
    private boolean isCorrect;

    public Option() {
    }

    public Option(String option, boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }
    public void setOption(String option) {
        this.option = option;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }

    @Override
    public String toString() {
        return "Option{" +
                "option='" + option + '\'' +
                ", isCorrect=" + isCorrect +
                '}';
    }


}
