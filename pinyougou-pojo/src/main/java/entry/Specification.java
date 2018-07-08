package entry;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

public class Specification implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TbSpecificationOption> tbSpecificationOption;
	private TbSpecification tbSpecification;
	public List<TbSpecificationOption> getTbSpecificationOption() {
		return tbSpecificationOption;
	}
	public void setTbSpecificationOption(List<TbSpecificationOption> tbSpecificationOption) {
		this.tbSpecificationOption = tbSpecificationOption;
	}
	public TbSpecification getTbSpecification() {
		return tbSpecification;
	}
	public void setTbSpecification(TbSpecification tbSpecification) {
		this.tbSpecification = tbSpecification;
	}
	
	
	
	
}
