/**
 * 
 */
package org.prabal.scheduler.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;

/**
 * @author Prabal Nandi
 *
 */
@XmlSimpleName(SchedulerNamespaceHandler.JOB_DATA_MAP)
public class JobDataMapProcessor implements ElementProcessor<Map<String, String>> {

   @Override
   public Map<String, String> process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      Map<String, String> dataMap = new HashMap<String, String>(xmlElement.getElementList().size());
      for(XmlElement element : (List<XmlElement>)xmlElement.getElementList()){
         dataMap.put(element.getAttributeMap().get(SchedulerNamespaceHandler.DATA_MAP_ATTRIBUTE_KEY).toString(), element.getValue().toString().trim());
      }
      return dataMap;
   }

}
