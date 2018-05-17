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
        	else if (this.i > this.max - 30)
            	t = (this.i - this.max + 35) * 10;
            setTimeout(this.run, t);
        } else {
            if (this.onFinish != null)
                this.onFinish();
        }
    };
};
