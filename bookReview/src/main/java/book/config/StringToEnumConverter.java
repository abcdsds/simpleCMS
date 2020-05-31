package book.config;



import org.springframework.core.convert.converter.Converter;

import book.modules.post.vote.VoteType;

public class StringToEnumConverter implements Converter<String, VoteType>{

	@Override
	public VoteType convert(String source) {
		// TODO Auto-generated method stub
		return VoteType.valueOf(source.toUpperCase());
	}


}
