function Tiger1() {
	this.i = 0;
    this.x = 0;
    this.y = 0;
    this.w = 20;
    this.h = 20;
	this.max = 0;
	this.onFinish = null
	this.element = null;
	this.pos = [[0,0],[0,1],[0,2],[1,2],[2,2],[2,1],[2,0],[1,0]];
    this.go = (d, l, of, x, y, w, h) => {
        if (this.i < this.max)
            return;
        this.element = d;
        this.i = 0;
        this.max = l;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.onFinish = of;
        setTimeout(this.run, 200);
    };
    this.run = () => {
        var xy = this.pos[this.i % 8];
        var e = $("#" + this.element);
        pot(this.element, this.x + xy[0] * this.w, this.y + xy[1] * this.h);
        if (this.i == 0)
            e.show();
        this.i++;
        if (this.i < this.max) {
        	var t = 50;
        	if (this.i < 20)
        		t = (29 - this.i) * 5;
        	else if (this.i >= this.max - 20)
            	t = Math.pow(this.i - this.max + 21, 2) + 50;
            setTimeout(this.run, t);
        } else {
            if (this.onFinish != null)
                this.onFinish();
        }
    };
};


function star1(div, num, w, h, size){
    for(var ii = 0; ii < num; ii++){
        var xx = Math.floor(Math.random()*w/3)+(ii%3)*w/3;
        var yy = Math.floor(Math.random()*h/3)+Math.floor((ii%9)/3)*h/3;
        var ww = Math.floor(Math.random()*size+size/2);
        pot(div+"_"+ii , xx - ww/2, yy - ww/2,  ww, ww);
        $("#"+div+"_"+ii).attr("src", "https://static.iyunbao.com/website/health/iyb/resource/activity/gpo/images/timemachine/xingxing_"+Math.floor(Math.random()*3+1)+".png");
    }
}