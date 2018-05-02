package cn.edu.swust.taskscheduing.asRank;

import java.util.Comparator;

import cn.edu.swust.taskschecuing.ant.Ant;
//
public class AntComparator implements Comparator<Ant> {
	@Override
	public int compare(Ant a1, Ant a2) { 
		return a1.getTimecostTotle().compareTo(a2.getTimecostTotle());
	}
}
