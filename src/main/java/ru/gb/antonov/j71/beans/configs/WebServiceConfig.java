package ru.gb.antonov.j71.beans.configs;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter
{
/** Здесь мы создаём и настраиваем отдельный сервлет для SOAP.<p>
    Если неправильно ввести адрес, то сообщение об ошибке придёт в html-формате (404).<p>
    Если неправильно указать название wsdl-файла, то придёт 405.
*/
    public WebServiceConfig() {}

/** {@code "/ws/*"} — это адрес, на который нужно слать запросы этому сервлету. Оказалось, что этот адрес добавляется к {@code http://localhost:8189/market} — т.е. звено {@code /market} категорически показано к применению. Без него ничего не получится. */
    @Bean
    public ServletRegistrationBean messageDispatcherServlet (ApplicationContext applicationContext)
    {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet ();
        servlet.setApplicationContext (applicationContext);
        servlet.setTransformWsdlLocations (true);
        return new ServletRegistrationBean (servlet, "/ws/*");
    }

/** Как оказалось, инструкция <br>{@code @Bean (name = {"productSoap"})}<br>указывает на название wsdl-файла, который будет создан и отправлен в ответ на запрос wsdl-файла. Т.е. запрос wsdl-файла должен выглядеть так:<br>{@code http://localhost:8189/market/ws/productSoap.wsdl}. */
    @Bean (name = {"productSoap"})
    public DefaultWsdl11Definition productWsdl11Definition (XsdSchema productSchema)
    {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition ();
        wsdl11Definition.setPortTypeName ("ProductPort");
        wsdl11Definition.setLocationUri ("/ws");
        wsdl11Definition.setTargetNamespace ("http://j71.antonov.gb.ru/spring/ws/product");
        wsdl11Definition.setSchema (productSchema);
        return wsdl11Definition;
    }

/** В отладчике этот метод всегда рапортует об исключении {@code IllegalArgumentException}. */
    @Bean
    public XsdSchema productSchema ()
    {
        return new SimpleXsdSchema (new ClassPathResource ("xsd/product.xsd"));
    }
}
