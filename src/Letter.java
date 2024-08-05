public class Letter
{
   public String value;
   public String backgroundColor;
   Letter(String n)
   {
      value = n;
      backgroundColor = "#FFFFFF";
   }
   public String getValue()
   {
      return value;
   }
   public void setColor(String c)
   {
    backgroundColor = c;
   }
   public String getColor()
   {
      return backgroundColor;
   }
}