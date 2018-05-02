package cn.edu.swust.myori;

import java.util.List;

public class Ant {
	public int Num_ant;
	public float[][] tao; //信息素
	public float[][] delta;//释放信息素
	public List tabu; //禁忌表
	public List allowed;//可访问的表
	
	public int bestLength;
	public List bestTour;
	public int tourLength; //总距离
	
	public float[][]p;
}
