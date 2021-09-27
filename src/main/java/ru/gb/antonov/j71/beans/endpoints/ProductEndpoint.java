package ru.gb.antonov.j71.beans.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.beans.soap.products.GetProductSoapByIdRequest;
import ru.gb.antonov.j71.beans.soap.products.GetProductSoapByIdResponse;
import ru.gb.antonov.j71.beans.soap.products.GetProductSoapRangeByIdRequest;
import ru.gb.antonov.j71.beans.soap.products.GetProductSoapRangeByIdResponse;

@Endpoint
@RequiredArgsConstructor
public class ProductEndpoint
{
    private static final String NAMESPACE_URI = "http://j71.antonov.gb.ru/spring/ws/product";
    private final ProductService productService;

    @PayloadRoot (namespace = NAMESPACE_URI, localPart = "getProductSoapByIdRequest")
    @ResponsePayload
    public GetProductSoapByIdResponse getProductSoapById (
                                        @RequestPayload GetProductSoapByIdRequest request)
    {
        GetProductSoapByIdResponse response = new GetProductSoapByIdResponse();
        response.setProductSoap (productService.getProductSoapByProductId (request.getId()));
        return response;
    }

    @PayloadRoot (namespace = NAMESPACE_URI, localPart = "getProductSoapRangeByIdRequest")
    @ResponsePayload
    public GetProductSoapRangeByIdResponse getProductSoapRangeById (
                                        @RequestPayload GetProductSoapRangeByIdRequest request)
    {
        GetProductSoapRangeByIdResponse response = new GetProductSoapRangeByIdResponse();
        response.setSoapProducts (productService.getProductSoapRangeByProductIdRange (
                                                        request.getFromId(), request.getToId()));
        return response;
    }
}
/*      Пример POST-запроса:  http://localhost:8189/market/ws

        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:f="http://j71.antonov.gb.ru/spring/ws/product">
            <soapenv:Header/>
            <soapenv:Body>
                <f:getProductSoapByIdRequest>
                    <f:id>11</f:id>
                </f:getProductSoapByIdRequest>
            </soapenv:Body>
        </soapenv:Envelope>

        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:f="http://j71.antonov.gb.ru/spring/ws/product">
            <soapenv:Header/>
            <soapenv:Body>
                <f:getProductSoapRangeByIdRequest>
                    <f:fromId>1</f:fromId>
                    <f:toId>2</f:toId>
                </f:getProductSoapRangeByIdRequest>
            </soapenv:Body>
        </soapenv:Envelope>     */