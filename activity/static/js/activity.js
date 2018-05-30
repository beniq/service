var zoom = function(){
    var xx = document.getElementById("ccc");
    return xx.offsetWidth / 750;
};
var zoomY = function(){
    var xx = document.getElementById("ccc");
    return xx.offsetHeight / ENV.H;
};
var pot = function(name, x, y, w, h, tp)
{
    var rh = h == -1 ? window.innerHeight : zoomY() * h;
    var d5 = document.getElementById(name);
    d5.style.width = zoom() * w;
    d5.style.height = rh;
    d5.style.position = tp ? tp : "absolute";
    d5.style.left = zoom() * x;
    d5.setAttribute("h", rh);
    if (y == -10000) {
        d5.style.top = (Number(d5.parentNode.getAttribute("h")) - rh) / 2;
    } else if (y == -20000) {
        if (tp == "fixed") {
            d5.style.top = window.innerHeight - rh;
        } else {
            d5.style.top = (Number(d5.parentNode.getAttribute("h")) - rh);
        }
    } else {
        d5.style.top = zoomY() * y;
    }
};
var fnt = function(name, size1, size2)
{
    try{
        var obj = document.getElementById(name);
        obj.style.fontSize = (zoom() * size1)+'px';
        obj.style.lineHeight = (zoom() * size2)+'px';
    }catch(e){}
};
var hidedown = function(e)
{
    e.stopPropagation();
    e.preventDefault();
};

(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        //AMD
        define(factory);
    } else if (typeof exports === 'object') {
        //Node, CommonJS之类的
        module.exports = factory();
    } else {
        //浏览器全局变量(root 即 window)
        root.resLoader = factory(root);
    }
}(this, function () {
    var isFunc = function (f) {
        return typeof f === 'function';
    };
    //构造器函数
    function resLoader(config) {
        this.option = {
            resourceType: 'image', //资源类型，默认为图片
            baseUrl: './', //基准url
            resources: [], //资源路径数组
            onStart: null, //加载开始回调函数，传入参数total
            onProgress: null, //正在加载回调函数，传入参数currentIndex, total
            onComplete: null //加载完毕回调函数，传入参数total
        };
        if (!!config) {
            for (i in config) {
                this.option[i] = config[i];
            }
        }
        else {
            console.log('参数错误！');
            return;
        }
        this.status = 0; //加载器的状态，0：未启动   1：正在加载   2：加载完毕
        this.total = this.option.resources.length || 0; //资源总数
        this.currentIndex = 0; //当前正在加载的资源索引
    };

    resLoader.prototype.start = function () {
        this.status = 1;
        var _this = this;
        var baseUrl = this.option.baseUrl;
        for (var i = 0, l = this.option.resources.length; i < l; i++) {
            var r = this.option.resources[i], url = '';
            if (r.indexOf('http://') === 0 || r.indexOf('https://') === 0) {
                url = r;
            }
            else {
                url = baseUrl + r;
            }
            var image = new Image();
            image.onload = function () {
                _this.loaded();
            };
            image.onerror = function () {
                _this.loaded();
            };
            image.src = url;
        }
        if (isFunc(this.option.onStart)) {
            this.option.onStart(this.total);
        }
    };

    resLoader.prototype.loaded = function () {
        if (isFunc(this.option.onProgress)) {
            this.option.onProgress(++this.currentIndex, this.total);
        }
        //加载完毕
        if (this.currentIndex === this.total && this.status != 2) {
            if (isFunc(this.option.onComplete)) {
                this.option.onComplete(this.total);
            }
            this.status = 2;
        }
    };

    //暴露公共方法
    return resLoader;
}));


var ResLoader = {
    option: {
        status: 0,
        resourceType: 'image', //资源类型，默认为图片
        baseUrl: './', //基准url
        resources: [], //资源路径数组
        onStart: null, //加载开始回调函数，传入参数total
        onProgress: null, //正在加载回调函数，传入参数currentIndex, total
        onComplete: null //加载完毕回调函数，传入参数total
    },
    start: function (self) {
        ResLoader.option.baseUrl = self.baseUrl;
        ResLoader.option.resources = self.resources;
        ResLoader.option.onStart = self.onStart;
        ResLoader.option.onProgress = self.onProgress;
        ResLoader.option.onComplete = self.onComplete;
        ResLoader.total = self.resources.length || 0; //资源总数
        ResLoader.status = 1;
        ResLoader.currentIndex = 0;
        var baseUrl = ResLoader.option.baseUrl;
        for (var i = 0, l = ResLoader.option.resources.length; i < l; i++) {
            var r = ResLoader.option.resources[i], url = '';
            if (r.indexOf('http://') === 0 || r.indexOf('https://') === 0) {
                url = r;
            }
            else {
                url = baseUrl + r;
            }
            var image = new Image();
            image.onload = function () {
                ResLoader.loaded();
            };
            image.onerror = function () {
                ResLoader.loaded();
            };
            image.src = url;
        }
        if (ResLoader.option.onStart) {
            ResLoader.option.onStart(ResLoader.total);
        }
    },
    loaded: function () {
        if (ResLoader.option.onProgress) {
            ResLoader.option.onProgress(++ResLoader.currentIndex, ResLoader.total);
        }
        //加载完毕
        if (ResLoader.currentIndex === ResLoader.total && ResLoader.status != 2) {
            if (ResLoader.option.onComplete) {
                ResLoader.option.onComplete(ResLoader.total);
            }
            ResLoader.status = 2;
        }
    }
}
