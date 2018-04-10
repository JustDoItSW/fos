package com.fos.myView;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Apersonalive（丁起柠） on 2018/3/24 19 21.
 * Project_name TemperatureTest
 * Package_name dqn.demo.com.temperaturetest.Util
 * Email 745267209@QQ.com
 */

public class MyLineChart {
    public LineChartData lineChartData;
    public LineChartView lineChartView;
    public List<Line> lineList;
    public List<PointValue> pointValueList;
    public List<AxisValue> axisXValueList,axisYValueList;
    public Axis axisX,axisY;
    public String sign;
    public static int TOP,BOTTOM,LEFT,RIGHT;
    private int count = 1;


    public MyLineChart(int TOP,int BOTTOM,int LEFT,int RIGHT,LineChartView lCV,String sign){
        this.TOP = TOP;
        this.BOTTOM = BOTTOM;
        this.LEFT = LEFT;
        this.RIGHT = RIGHT;

        this.sign =  sign;
        lineChartView = lCV;
        lineChartData = new LineChartData();
        pointValueList = new ArrayList<>();
        lineList = new ArrayList<>();
        axisXValueList = new ArrayList<>();
        axisYValueList = new ArrayList<>();
        initAxisX();
        initAxisY();
        initLine();
        initDatas(lineList);
        lineChartView.setLineChartData(lineChartData);
        lineChartView.setBackgroundColor(Color.WHITE);
        lineChartView.setMaximumViewport(initViewPort(TOP,BOTTOM,LEFT,RIGHT));
        lineChartView.setCurrentViewport(initViewPort(30,0,0,6));
        moveViewPort(3,16);
        lineChartView.setInteractive(true);
        lineChartView.setScrollEnabled(true);
        lineChartView.setValueTouchEnabled(false);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setZoomEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();
    }
    /**
     * 初始化横坐标
     */
    public void initAxisX(){
        axisX = new Axis();
        axisX.setName("时间");
        axisX.setTextColor(Color.rgb(181,179,170));
        axisX.setHasLines(true);
        axisX.setLineColor(Color.rgb(235,235,235));

        for(int i = 0;i<=60;i++){
            axisXValueList.add(new AxisValue(i).setLabel(""));
        }

        axisX.setValues(axisXValueList);
        lineChartData.setAxisXBottom(axisX);
    }

    /**
     * 设置X坐标
     * @param date
     */
    public void setAxisXLabel(String date){
        axisXValueList.get(count).setLabel(date);
        axisX.setValues(axisXValueList);
        lineChartData.setAxisXBottom(axisX);
    }

    /**
     * 初始化纵坐标
     */
    public void initAxisY(){
        axisY = new Axis();
        axisY.setName("温度/℃");
        axisY.setTextColor(Color.BLACK);
        List<AxisValue> axisValueList = new ArrayList<>();
        for(int i = 0;i<=100;i++){
            axisValueList.add(new AxisValue(i).setLabel(i+""));
        }
        axisY.setValues(axisValueList);
  //      lineChartData.setAxisYLeft(axisY);
    }

    /**
     * 初始化折线图数据
     * @param lineList
     * @return
     */
    public LineChartData initDatas(List<Line> lineList) {
        lineChartData.setLines(lineList);
        lineChartData.setValueLabelBackgroundColor(Color.TRANSPARENT);
        lineChartData.setValueLabelBackgroundEnabled(false);
        lineChartData.setValueLabelsTextColor(Color.GRAY);
        return lineChartData;
    }
    /**
     * 初始化视图范围
     * @param left
     * @param right
     * @return
     */
    public Viewport initViewPort(float top, float bottom, float left, float right) {
        Viewport port = new Viewport();
        port.top = top;
        port.bottom = bottom;
        port.left = left;
        port.right = right;
        return port;
    }

    public void initLine(){
        List<PointValue> pointValueList = new ArrayList<>();
        for(int i = 1;i<=24;){
            pointValueList.add(new PointValue(i,25));
            i+=1;
        }
        Line line = new Line(pointValueList);
        line.setFilled(true);
        line.setHasPoints(true);
        line.setHasLabels(true);
        line.setPointColor(Color.WHITE);
        line.setPointRadius(2);
        line.setColor(Color.GRAY);
        line.setStrokeWidth(1);
        lineList.add(line);
    }

    /**
     * 重绘折现
     * @param
     * @param color
     */
    public void repaintView(int info,String date,int color){
        if(count<60) {
            PointValue newPointValue = new PointValue(count, info);
            newPointValue.setLabel(info+sign);
            pointValueList.add(newPointValue);

            Line line = new Line(pointValueList);
            line.setStrokeWidth(1);
            line.setColor(color);
            line.setPointRadius(3);
            line.setHasLabels(true);
            line.setFilled(true);

            line.setPointColor(color);
            line.setCubic(true);
            lineList = new ArrayList<>();
            lineList.add(line);

            Line pointLine = new Line(pointValueList);
            pointLine.setPointColor(Color.WHITE);
            pointLine.setPointRadius(2);
            pointLine.setHasLines(false);

            lineList.add(pointLine);
            lineChartData.setLines(lineList);
            setAxisXLabel(date);
            lineChartView.setLineChartData(lineChartData);

            moveViewPort(count,info);
            count++;
        }else {
            //重新绘制折线图
            count = 1;
            pointValueList = new ArrayList<>();
            axisXValueList = new ArrayList<>();
            lineList = new ArrayList<>();
            lineChartData = initDatas(null);
            lineChartView.setLineChartData(lineChartData);
        }

    }
    /**
     * 移动镜头
     * @param x
     */
    public void  moveViewPort(float x,float y){
        Viewport viewport;
        if(x<3)
            lineChartView.moveToWithAnimation(3,y);
        if(x>=3 && x<21){
            lineChartView.moveToWithAnimation(x,y);
        }
    }
}
