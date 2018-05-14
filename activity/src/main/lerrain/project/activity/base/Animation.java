package lerrain.project.activity.base;

public class Animation
{
    long startTime;
    long keepTime; //持续时间

    int loop; //循环次数，-1永久
    int mode; //加速模式

    float x, y; //开始位置，相对原始位置，0，0代表当前位置
    float mx, my;

    int fromArc; //从多少度开始旋转
    int rotateArc; //旋转多少度

    boolean clockwise = true;

}
