
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.AudioDecoderConfiguration;


/**
 * <prop>Java class for anonymous complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Configurations" type="{http://www.onvif.org/ver10/schema}AudioDecoderConfiguration" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configurations"
})
@XmlRootElement(name = "GetAudioDecoderConfigurationsResponse")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
public class GetAudioDecoderConfigurationsResponse {

    @XmlElement(name = "Configurations")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    protected List<AudioDecoderConfiguration> configurations;

    /**
     * Gets the value of the configurations property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configurations property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfigurations().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link AudioDecoderConfiguration }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public List<AudioDecoderConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<AudioDecoderConfiguration>();
        }
        return this.configurations;
    }

}
