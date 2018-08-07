package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl;

import javax.annotation.Generated;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.2.5
 * 2018-08-07T08:47:23.076-05:00
 * Generated source version: 3.2.5
 *
 */
@WebService(targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", name = "ImagingPort")
@XmlSeeAlso({ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.oasis_open.docs.wsrf.bf_2.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.w3._2004._08.xop.include.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.oasis_open.docs.wsn.b_2.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.oasis_open.docs.wsn.t_1.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.w3._2003._05.soap_envelope.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.org.w3._2005._05.xmlmime.ObjectFactory.class})
@Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00", comments = "Apache CXF 3.2.5")
public interface ImagingPort {

    /**
     * Via this command the current status of the Move operation can be requested. Supported for this command is available if the support for the Move operation is signalled via GetMoveOptions.
     */
    @WebMethod(operationName = "GetStatus", action = "http://www.onvif.org/ver20/imaging/wsdl/GetStatus")
    @RequestWrapper(localName = "GetStatus", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetStatus")
    @ResponseWrapper(localName = "GetStatusResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetStatusResponse")
    @WebResult(name = "Status", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingStatus20 getStatus(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );

    /**
     * This operation gets the valid ranges for the imaging parameters that have device specific ranges. 
     * 			This command is mandatory for all device implementing the imaging service. The command returns all supported parameters and their ranges 
     * 			such that these can be applied to the SetImagingSettings command.
     * 			For read-only parameters which cannot be modified via the SetImagingSettings command only a single option or identical Min and Max values 
     * 			is provided.
     */
    @WebMethod(operationName = "GetOptions", action = "http://www.onvif.org/ver20/imaging/wsdl/GetOptions")
    @RequestWrapper(localName = "GetOptions", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetOptions")
    @ResponseWrapper(localName = "GetOptionsResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetOptionsResponse")
    @WebResult(name = "ImagingOptions", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingOptions20 getOptions(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );

    /**
     * Via this command the last Imaging Preset applied can be requested. 
     * 			If the camera configuration does not match any of the existing Imaging Presets, the output of GetCurrentPreset shall be Empty.
     * 			GetCurrentPreset shall return 0 if Imaging Presets are not supported by the Video Source.
     */
    @WebMethod(operationName = "GetCurrentPreset", action = "http://www.onvif.org/ver20/imaging/wsdl/GetCurrentPreset")
    @RequestWrapper(localName = "GetCurrentPreset", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetCurrentPreset")
    @ResponseWrapper(localName = "GetCurrentPresetResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetCurrentPresetResponse")
    @WebResult(name = "Preset", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.ImagingPreset getCurrentPreset(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );

    /**
     * Via this command the list of available Imaging Presets can be requested.
     */
    @WebMethod(operationName = "GetPresets", action = "http://www.onvif.org/ver20/imaging/wsdl/GetPresets")
    @RequestWrapper(localName = "GetPresets", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetPresets")
    @ResponseWrapper(localName = "GetPresetsResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetPresetsResponse")
    @WebResult(name = "Preset", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public java.util.List<us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.ImagingPreset> getPresets(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );

    /**
     * Get the ImagingConfiguration for the requested VideoSource.
     */
    @WebMethod(operationName = "GetImagingSettings", action = "http://www.onvif.org/ver20/imaging/wsdl/GetImagingSettings")
    @RequestWrapper(localName = "GetImagingSettings", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettings")
    @ResponseWrapper(localName = "GetImagingSettingsResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettingsResponse")
    @WebResult(name = "ImagingSettings", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20 getImagingSettings(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );

    /**
     * The SetCurrentPreset command shall request a given Imaging Preset to be applied to the specified Video Source.
     * 			SetCurrentPreset shall only be available for Video Sources with Imaging Presets Capability. 
     * 			Imaging Presets are defined by the Manufacturer, and offered as a tool to simplify Imaging Settings adjustments for specific scene content.
     * 			When the new Imaging Preset is applied by SetCurrentPreset, the Device shall adjust the Video Source settings to match those defined by the specified Imaging Preset.
     */
    @WebMethod(operationName = "SetCurrentPreset", action = "http://www.onvif.org/ver20/imaging/wsdl/SetCurrentPreset")
    @RequestWrapper(localName = "SetCurrentPreset", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetCurrentPreset")
    @ResponseWrapper(localName = "SetCurrentPresetResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetCurrentPresetResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public void setCurrentPreset(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken,
        @WebParam(name = "PresetToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String presetToken
    );

    /**
     * The Move command moves the focus lens in an absolute, a relative or in a continuous manner from its current position. 
     * 			The speed argument is optional for absolute and relative control, but required for continuous. If no speed argument is used, the default speed is used. 
     * 			Focus adjustments through this operation will turn off the autofocus. A device with support for remote focus control should support absolute, 
     * 			relative or continuous control through the Move operation. The supported MoveOpions are signalled via the GetMoveOptions command.
     * 			At least one focus control capability is required for this operation to be functional. 
     * 			The move operation contains the following commands:
     * 				 – Requires position parameter and optionally takes a speed argument. A unitless type is used by default for focus positioning and speed. Optionally, if supported, the position may be requested in m-1 units. 
     * 				 – Requires distance parameter and optionally takes a speed argument. Negative distance means negative direction. 
     * 			 – Requires a speed argument. Negative speed argument means negative direction.
     * 			
     */
    @WebMethod(operationName = "Move", action = "http://www.onvif.org/ver20/imaging/wsdl/Move")
    @RequestWrapper(localName = "Move", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.Move")
    @ResponseWrapper(localName = "MoveResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.MoveResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public void move(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken,
        @WebParam(name = "Focus", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FocusMove focus
    );

    /**
     * The Stop command stops all ongoing focus movements of the lense. A device with support for remote focus control as signalled via 
     * 			the GetMoveOptions supports this command. The operation will not affect ongoing autofocus operation.
     */
    @WebMethod(operationName = "Stop", action = "http://www.onvif.org/ver20/imaging/wsdl/FocusStop")
    @RequestWrapper(localName = "Stop", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.Stop")
    @ResponseWrapper(localName = "StopResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.StopResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public void stop(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );

    /**
     * Returns the capabilities of the imaging service. The result is returned in a typed answer.
     */
    @WebMethod(operationName = "GetServiceCapabilities", action = "http://www.onvif.org/ver20/imaging/wsdl/GetServiceCapabilities")
    @RequestWrapper(localName = "GetServiceCapabilities", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetServiceCapabilities")
    @ResponseWrapper(localName = "GetServiceCapabilitiesResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetServiceCapabilitiesResponse")
    @WebResult(name = "Capabilities", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.Capabilities getServiceCapabilities();

    /**
     * Set the ImagingConfiguration for the requested VideoSource.
     */
    @WebMethod(operationName = "SetImagingSettings", action = "http://www.onvif.org/ver20/imaging/wsdl/SetImagingSettings")
    @RequestWrapper(localName = "SetImagingSettings", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings")
    @ResponseWrapper(localName = "SetImagingSettingsResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public void setImagingSettings(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken,
        @WebParam(name = "ImagingSettings", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20 imagingSettings,
        @WebParam(name = "ForcePersistence", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.Boolean forcePersistence
    );

    /**
     * Imaging move operation options supported for the Video source.
     */
    @WebMethod(operationName = "GetMoveOptions", action = "http://www.onvif.org/ver20/imaging/wsdl/GetMoveOptions")
    @RequestWrapper(localName = "GetMoveOptions", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptions")
    @ResponseWrapper(localName = "GetMoveOptionsResponse", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptionsResponse")
    @WebResult(name = "MoveOptions", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-08-07T08:47:23.076-05:00")
    public us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.MoveOptions20 getMoveOptions(
        @WebParam(name = "VideoSourceToken", targetNamespace = "http://www.onvif.org/ver20/imaging/wsdl")
        java.lang.String videoSourceToken
    );
}
