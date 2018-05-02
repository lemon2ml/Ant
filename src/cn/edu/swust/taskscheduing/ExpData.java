package cn.edu.swust.taskscheduing;

import java.util.concurrent.ThreadLocalRandom;

public class ExpData {
	public int taskNum;
	public int vmNum;
	public Integer [] mi;//����
	public Integer [] mip;//�������Դ
	public Float[][] etc;
	
	public void init(int taskNum,int vmNum,int miMin,int miMax,int mipMin,int mipMax){
		this.taskNum=taskNum;
		this.vmNum=vmNum;
		mi=new Integer[taskNum];
		mip=new Integer[vmNum];
		etc=new Float[taskNum][vmNum];  
		for(int i=0;i<taskNum;i++){//����������� 300-3600 
	        mi[i]=miMin+ThreadLocalRandom.current().nextInt(miMax-miMin);
		}  
		for(int i=0;i<vmNum;i++){//������������ 500-1500
			
	        mip[i]=mipMin+ThreadLocalRandom.current().nextInt(mipMax-mipMin);
		}
		
		for(int i=0;i<taskNum;i++)
			for(int j=0;j<vmNum;j++){//���Ƽ���ִ��ʱ��
			etc[i][j]=Float.valueOf(mi[i])/mip[j];			
		}
	}

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public int getVmNum() {
		return vmNum;
	}

	public void setVmNum(int vmNum) {
		this.vmNum = vmNum;
	}

	public Integer[] getMi() {
		return mi;
	}

	public void setMi(Integer[] mi) {
		this.mi = mi;
	}

	public Integer[] getMip() {
		return mip;
	}

	public void setMip(Integer[] mip) {
		this.mip = mip;
	}

	public Float[][] getEtc() {
		return etc;
	}

	public void setEtc(Float[][] etc) {
		this.etc = etc;
	}
}
