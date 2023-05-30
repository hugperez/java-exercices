package com.javaexercises.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "note")
    private Integer note;

    @Column(name = "content")
    private String content;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "total_weight")
    private Float totalWeight;

    @Column(name = "is_verified")
    private Boolean isVerified;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNote() {
        return this.note;
    }

    public Product note(Integer note) {
        this.setNote(note);
        return this;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getContent() {
        return this.content;
    }

    public Product content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Product quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getWeight() {
        return this.weight;
    }

    public Product weight(Float weight) {
        this.setWeight(weight);
        return this;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getTotalWeight() {
        return this.totalWeight;
    }

    public Product totalWeight(Float totalWeight) {
        this.setTotalWeight(totalWeight);
        return this;
    }

    public void setTotalWeight(Float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public Product isVerified(Boolean isVerified) {
        this.setIsVerified(isVerified);
        return this;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", note=" + getNote() +
            ", content='" + getContent() + "'" +
            ", quantity=" + getQuantity() +
            ", weight=" + getWeight() +
            ", totalWeight=" + getTotalWeight() +
            ", isVerified='" + getIsVerified() + "'" +
            "}";
    }
}
