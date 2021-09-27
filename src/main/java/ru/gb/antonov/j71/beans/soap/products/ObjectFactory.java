package ru.gb.antonov.j71.beans.soap.products;

import javax.xml.bind.annotation.XmlRegistry;

/**
    Этот объект содержит фабричные методы для каждого интерфейса содержимого Java и интерфейса элемента Java, созданного в пакете ru.gb.antonov.j71.beans.soap.<p>
ObjectFactory позволяет программно создавать новые экземпляры представления Java для содержимого XML. Представление Java содержимого XML может состоять из интерфейсов, производных от схемы, и классов, представляющих привязку определений типов схемы, объявлений элементов и групп моделей. Фабричные методы для каждого из них в этом классе представлены.

<p><b>Оригинал самого понятного (по обе стороны Атлантики) описания:</b><i>
 * This object contains factory methods for each
 * Java content interface and Java element interface 
 * generated in the ru.gb.antonov.j71.beans.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.</i>
 * 
 <p><b>Чёрная неблагодарность:</b>
 Методы, написанные руками, прекрасно работают без участия этой «фабрики». */
@XmlRegistry
public class ObjectFactory
{
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.gb.antonov.j71.beans.soap
     * 
     */
    public ObjectFactory () {}

    /**
     Create an instance of {@link GetProductSoapByIdRequest }
     */
    public GetProductSoapByIdRequest createGetProductSoapByIdRequest ()
    {
        return new GetProductSoapByIdRequest ();
    }

    /**
     Create an instance of {@link GetProductSoapByIdResponse }
     */
    public GetProductSoapByIdResponse createGetProductSoapByIdResponse ()
    {
        return new GetProductSoapByIdResponse ();
    }

    public GetProductSoapRangeByIdRequest createGetProductSoapRangeByIdRequest ()
    {
        return new GetProductSoapRangeByIdRequest ();
    }

    public GetProductSoapRangeByIdResponse createGetProductSoapRangeByIdResponse ()
    {
        return new GetProductSoapRangeByIdResponse ();
    }

    /**
     Create an instance of {@link ProductSoap }
     */
    public ProductSoap createProductSoap ()
    {
        return new ProductSoap ();
    }
}
