package cn.edu.swust.myori;

import java.util.List;

public class Ant {
	public int Num_ant;
	public float[][] tao; //��Ϣ��
	public float[][] delta;//�ͷ���Ϣ��
	public List tabu; //���ɱ�
	public List allowed;//�ɷ��ʵı�
	
	public int bestLength;
	public List bestTour;
	public int tourLength; //�ܾ���
	
	public float[][]p;
}
