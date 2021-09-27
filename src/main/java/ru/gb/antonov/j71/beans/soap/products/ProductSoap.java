package ru.gb.antonov.j71.beans.soap.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSoap",
         propOrder = {"id", "title", "price", "rest", "categoryName", "createdAt", "updatedAt"})
public class ProductSoap
{
    @XmlElement(required=true)    protected long id;
    @XmlElement(required=true)    protected String title;
    @XmlElement(required=true)    protected double price;
    @XmlElement(required=true)    protected int rest;
    @XmlElement(required=true)    protected String categoryName/* = new ProductsCategorySoap (p.getCategory())*/;
    @XmlElement(required=true)    protected long createdAt;
    @XmlElement(required=true)    protected long updatedAt;

    public ProductSoap (){}
    public ProductSoap (long pId, String pTitle, double pPrice, int pRest,
                        String pCategoryName, long pCreatedAt, long pUpdatedAt)
    {   id        = pId;
        title     = pTitle;
        price     = pPrice;
        rest      = pRest;
        categoryName = pCategoryName;
        createdAt = pCreatedAt;
        updatedAt = pUpdatedAt;
    }
//-----------------------------------------------------------------------
    public long getId ()    { return id; }
    public void setId (long value)    { id = value; }

    public String getTitle ()    { return title; }
    public void   setTitle (String value)    { title = value; }

    public double getPrice ()    { return price; }
    public void   setPrice (double value)    { price = value; }

    public int  getRest ()    { return rest; }
    public void setRest (int value)    { rest = value; }

    public String getCategoryName () { return categoryName; }
    public void   setCategoryName (String value) { categoryName = value; }

    public long getCreatedAt ()    { return createdAt; }
    public void setCreatedAt (long value)    { createdAt = value; }

    public long getUpdatedAt ()    { return updatedAt; }
    public void setUpdatedAt (long value) { updatedAt = value; }
}
