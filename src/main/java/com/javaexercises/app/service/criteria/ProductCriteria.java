package com.javaexercises.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.javaexercises.app.domain.Product} entity. This class is used
 * in {@link com.javaexercises.app.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter note;

    private StringFilter content;

    private IntegerFilter quantity;

    private FloatFilter weight;

    private FloatFilter totalWeight;

    private BooleanFilter isVerified;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.note = other.note == null ? null : other.note.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.weight = other.weight == null ? null : other.weight.copy();
        this.totalWeight = other.totalWeight == null ? null : other.totalWeight.copy();
        this.isVerified = other.isVerified == null ? null : other.isVerified.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getNote() {
        return note;
    }

    public IntegerFilter note() {
        if (note == null) {
            note = new IntegerFilter();
        }
        return note;
    }

    public void setNote(IntegerFilter note) {
        this.note = note;
    }

    public StringFilter getContent() {
        return content;
    }

    public StringFilter content() {
        if (content == null) {
            content = new StringFilter();
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public FloatFilter getWeight() {
        return weight;
    }

    public FloatFilter weight() {
        if (weight == null) {
            weight = new FloatFilter();
        }
        return weight;
    }

    public void setWeight(FloatFilter weight) {
        this.weight = weight;
    }

    public FloatFilter getTotalWeight() {
        return totalWeight;
    }

    public FloatFilter totalWeight() {
        if (totalWeight == null) {
            totalWeight = new FloatFilter();
        }
        return totalWeight;
    }

    public void setTotalWeight(FloatFilter totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BooleanFilter getIsVerified() {
        return isVerified;
    }

    public BooleanFilter isVerified() {
        if (isVerified == null) {
            isVerified = new BooleanFilter();
        }
        return isVerified;
    }

    public void setIsVerified(BooleanFilter isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(note, that.note) &&
            Objects.equals(content, that.content) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(weight, that.weight) &&
            Objects.equals(totalWeight, that.totalWeight) &&
            Objects.equals(isVerified, that.isVerified) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, note, content, quantity, weight, totalWeight, isVerified, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (note != null ? "note=" + note + ", " : "") +
            (content != null ? "content=" + content + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (weight != null ? "weight=" + weight + ", " : "") +
            (totalWeight != null ? "totalWeight=" + totalWeight + ", " : "") +
            (isVerified != null ? "isVerified=" + isVerified + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
