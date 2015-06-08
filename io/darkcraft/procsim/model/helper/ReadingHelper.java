package io.darkcraft.procsim.model.helper;

public class ReadingHelper
{
	public static final String splitRegex = "(,( |	)*)|( |	)+";

	public static Integer literal(String in)
	{
		if(in == null) return null;
		if(!in.startsWith("#")) return null;
		in = in.substring(1);
		return Integer.parseInt(in);
	}

	public static Pair<String,String> getAddressingRegister(String adr)
	{
		if(adr.startsWith("[") && adr.endsWith("]"))
		{
			adr = adr.replace("[", "").replace("]", "");
			String[] data = adr.split("( |,)+",2);
			if(data.length == 2)
				return new Pair<String,String>(data[0],data[1]);
			return new Pair<String,String>(data[0],null);
		}
		return null;
	}
}
