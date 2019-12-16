
/**
 * 
 */
package org.prabal.scheduler.core;

import com.tangosol.net.cache.KeyAssociation;

/**
 * @author Prabal Nandi
 *
 */
public abstract class BaseKey implements Comparable<BaseKey>, Cloneable, KeyAssociation {

   public static final String DEFAULT_GROUP = "DEFAULT";
   public static final String JOB_KEY = "JOB_KEY";
   public static final String TRIGGER_KEY = "TRIGGER_KEY";
   public static final String RESULT_KEY = "RESULT_KEY";
   public static final String KEY_ASSOCIATOR = "KEY_ASSOCIATOR";

   private final String name;
   private final String group;
   private final String type;

   public BaseKey(String name, String group, String type) {
      super();
      this.name = name;
      this.group = (group == null || group.equals("")) ? DEFAULT_GROUP : group;
      this.type = type;
   }

   public String getName() {
      return name;
   }

   public String getGroup() {
      return group;
   }

   public String getType() {
      return type;
   }

   public String getUniqueString() {
      return type + "_" + name + "_" + group;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((group == null) ? 0 : group.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseKey other = (BaseKey) obj;
      // Check Group
      if (group == null) {
         if (other.group != null)
            return false;
      }
      else if (!group.equals(other.group))
         return false;
      // Check Name
      if (name == null) {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      // Check Type
      if (type == null) {
         if (other.type != null)
            return false;
      }
      else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public int compareTo(BaseKey o) {
      if (group.equals(DEFAULT_GROUP) && !o.group.equals(DEFAULT_GROUP))
         return -1;
      if (!group.equals(DEFAULT_GROUP) && o.group.equals(DEFAULT_GROUP))
         return 1;

      int r = group.compareTo(o.getGroup());
      if (r != 0)
         return r;

      r = name.compareTo(o.getName());
      if (r != 0)
         return r;

      return type.compareTo(o.getType());
   }

   @Override
   public String toString() {
      return "BaseKey [name=" + name + ", group=" + group + ", type=" + type + "]";
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException("Cloning to Key Not supported");
   }

}