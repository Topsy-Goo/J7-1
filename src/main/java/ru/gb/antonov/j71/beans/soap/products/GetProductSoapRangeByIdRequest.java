package ru.gb.antonov.j71.beans.soap.products;

import javax.xml.bind.annotation.*;

@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name="", propOrder={"fromId", "toId"})
@XmlRootElement (name="getProductSoapRangeByIdRequest")
public class GetProductSoapRangeByIdRequest
{
    @XmlElement (required=true) protected long fromId;
    @XmlElement (required=true) protected long toId;

    public long getFromId() { return fromId; }
    public void setFromId(long value) { fromId = value; }

    public long getToId() { return toId; }
    public void setToId(long value) { toId = value; }
}
