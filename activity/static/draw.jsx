var ENV = {
    W: 750,
    H: 1200,
    index: 0,
    counter: 1,
    ready: {},
    mapping: {},
    doc: {
        pages: []
    },
    event: {
        init: {text: "初始化"},
        certfiOnly: {text: "资格验证"},
        open: {text: "显示图层"},
        close: {text: "关闭图层"},
        closeAni: {
            text: "带动画关闭图层",
            comp: [
                {label: "关闭动画", code: "ani", type: "select", value: [
                    {code: "", text: "请选择"},
                    {code: "slideUp", text: "向上滑动"}
                ]}
            ]
        },
        play: {text: "轮播背景"},
        bgSwitch: {text: "切换背景"},
        tiger: {text: "转动抽奖机"},
        sparks: {text: "释放烟花"},
        stopSparks: {text: "关闭烟花"},
        scroll: {text: "滚动至此"},
        submit: {text: "提交表单"},
        nextPage: {
            text: "跳转下一页",
            comp: [
                {label: "动画", code: "ani", type: "select", value: [
                    {code: "", text: "请选择"},
                    {code: "slideUp", text: "向上滑动"}
                ]}
            ]
        },
        redirect: {
            text: "跳转至URL",
            comp: [
                {label: "URL", code: "url", type: "input"},
                {label: "JS表达式", code: "value", type: "input"},
                {label: "追加用户id", code: "accountId", type: "switch"}
            ]
        },
        js: {
            text: "执行js",
            comp: [{label: "JS", code: "value", type: "input"}]
        },
        toProduct: {
            text: "跳转至产品",
            comp: [
                {label: "产品", code: "value", type: "select", value: [{code: "", text: "请选择"}]}
            ]
        },
        shareProduct: {
            text: "转发产品",
            comp: [
                {label: "产品", code: null, type: "option", value: [{code: "", text: "请选择"}]},
                {label: "标题", code: "title", type: "input"},
                {label: "介绍", code: "desc", type: "input"},
                {label: "小图", code: "imgUrl", type: "input"},
                {label: "产品ID", code: "productId", type: "input"}
            ]
        }
    },
    style: {
        hide: {text: "隐藏"},
        fixed: {
            text: "浮动固定",
        },
        shake1: {
            text: "缩放抖动",
            comp: [{label: "开始", code: "begin", type: "input"}]
        },
        shake2: {
            text: "摇摆抖动",
            comp: [{label: "开始", code: "begin", type: "input"}]
        },
        shake3: {
            text: "大摇摆抖动",
            comp: [{label: "开始", code: "begin", type: "input"}]
        },
        shake4: {
            text: "放大抖动",
            comp: [{label: "开始", code: "begin", type: "input"}]
        },
        rotate: {
            text: "旋转",
            comp: [{label: "开始", code: "begin", type: "input"}]
        },
        float: {
            text: "上下漂浮",
            comp: [{label: "开始", code: "begin", type: "input"}]
        },
        textin: {
            text: "文字入场",
            comp: [
                {label: "开始", code: "begin", type: "input"},
                {label: "方向", code: "direct", type: "select", value: [
                    {code:"1", text:"左侧进入"},
                    {code:"2", text:"上侧进入"},
                    {code:"3", text:"右侧进入"},
                    {code:"4", text:"下侧进入"},
                ]},
                {label: "加速度", code: "spd", type: "select", value: [
                    {code:"linear", text:"线性"},
                    {code:"ease", text:"慢快慢"},
                    {code:"ease-in", text:"慢快"},
                    {code:"ease-out", text:"快慢"},
                ]},
            ]
        },
        progress: {
            text: "进度条",
            comp: [{label: "百分比JS", code: "percent", type: "input"}]
        },
        canvas: {text: "画板背景(烟花等)"},
        play: {text: "背景轮播"},
        popup: {text: "弹出动效"},
        alpha50: {text: "50%透明"},
        bgSwitch: {
            text: "背景(序号)",
            comp: [
                {label: "序号", code: "index", type: "input"}
            ]
        },
        bgUrl: {
            text: "背景(表达式)",
            comp: [
                {label: "URL", code: "url", type: "input"}
            ]
        },
        stars: {text: "金色星星"},
        autoScroll: {text: "滚动字幕"},
        scroll: {text: "可卷动"},
    },
    images: {},
    imagesOnload: {}
};

ENV.products = [
    {name: "全部", productId: 1, title: "全部", desc: "", imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/160000001/icon.jpg'},
    {name: "已选全部", productId: 2, title: "已选全部", desc: "", imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/160000001/icon.jpg'},
    {name: "百年康惠保重大疾病保险", productId: 1014999, title: '百年康惠保重大疾病保险', desc: '最高保额50万，1-6类职业可投保，100种重疾+30种轻症，可保终身。本产品支持人工核保，有需要请联系客服', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/160000001/icon.jpg'},
    {name: "复星联合康乐e生重大疾病保险", productId: 1004329, title: '复星联合康乐e生重大疾病保险', desc: '80种重疾35种轻症，身故责任可选，保费双豁免，保障缴费期限灵活', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/70000001/share_kangleyisheng.png'},
    {name: "和谐宝贝健康成长计划（升级）", productId: 1023622, title: '和谐宝贝健康成长计划（升级）', desc: '交费灵活，多重保障，月月复利，收益递增', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/60000002_3_4_5/hexiebaobei_share.png'},
    {name: "和谐健康之享定期重大疾病保险", productId: 1006015, title: '和谐健康之享定期重大疾病保险', desc: '50种重疾最高60万，次年保额翻倍 投保年龄：28天-50周岁 缴费期间：5/10/15/20年 本产品支持人工核保，有需要请联系客服', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/60000001/share_hexiezhixiang.png'},
    {name: "中宏宏创驾享意外险", productId: 1027430, title: '中宏宏创驾享意外险', desc: '无论驾驶、乘坐私家车的意外身故、伤残、意外医疗全保障，尊享全年2次免费代驾服务', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/390000001/zhiro_share.jpg'},
    {name: "平安驾乘人员意外伤害保险", productId: 1013515, title: '平安驾乘人员意外伤害保险', desc: '无论驾驶、乘坐汽车的意外都能保障，多种车型供选择 最高50万保额，一张保单保全车人员', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/car/110000003/110000003_share.jpg'},
    {name: "人保高额意外险", productId: 1019138, title: '人保高额意外险', desc: '意外身故、伤残最高600万', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/200000001_2_3_4_5_6/renbaogaoeyiwai_share.jpg'},
    {name: "上海人寿小蘑菇定期寿险", productId: 1012028, title: '上海人寿小蘑菇定期寿险', desc: '最高150万，全面涵盖身故和全残保障，女性费率市场最低，1-5类职业均可投保', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/130000001/iyb10005share.jpg'},
    {name: "上海人寿小蘑菇PLUS定期寿险", productId: 1025802, title: '上海人寿小蘑菇PLUS定期寿险', desc: '保额提升至300万！全面涵盖身故和全残保障！ 运用专业智能核保技术，AI赋能，享便捷高效投保！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/130000001/share_h.jpg'},
    {name: "同方全球「慧馨安」少儿保险产品计划", productId: 1020311, title: '同方全球「慧馨安」少儿保险产品计划', desc: '50种重疾+8种特定重疾双倍赔付！满期可返还保费，可附加投保人豁免！多种交费期限和保障期间灵活选择！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/190000002/share.png'},
    {name: "同方全球「同佑e生」保险产品计划", productId: 1020312, title: '同方全球「同佑e生」保险产品计划', desc: '涵盖100种重疾，保障高达50万！保障50种轻症，最多不分组3次赔付且豁免保费！可选70周岁或80周岁返还100%主附险累计已交保费！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/120000001/iyb10004share.jpg'},
    {name: "万元保住院医疗", productId: 1022555, title: '万元保住院医疗', desc: '保额1万元！0免赔！责任内医疗费用均100%报销!不限进口药、自费药，最高可续保至80周岁！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51339101/share_wanyuanbao.jpg'},
    {name: "信美相互爱我宝贝少儿白血病疾病保险", productId: 1020928, title: '信美相互爱我宝贝少儿白血病疾病保险', desc: '保障少儿高发疾病 最高50万保障 一天0.3元起', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/1010000001/share.jpg'},
    {name: "信泰i立方多次防癌险", productId: 1015008, title: '信泰i立方多次防癌险', desc: '确诊赔付3年后，未治愈、复发、转移、新确诊都能再赔，不分组3次赔付，累计最高可获赔90万', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/170000001/icon.jpg'},
    {name: "阳光人寿i保终身重大疾病保险", productId: 1022556, title: '阳光人寿i保终身重大疾病保险', desc: '涵盖100种重大疾病、20种轻症疾病、轻症豁免、身故四大保障！保障至终身，身故返还保额！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/230000001/logo.jpg'},
    {name: "众安安稳e生住院医疗保险（糖尿病和高血压专属版）", productId: 1026519, title: '安稳e生住院医疗险（糖尿病和高血压专属版）', desc: '保额高达50万元！最高可续保至80周岁！ 涵盖住院医疗保险金和特殊门诊医疗保险金！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51345402/anwenyisheng_share.jpg'},
    {name: "众安个人综合意外保险", productId: 1000017, title: '众安个人综合意外保险', desc: '意外身故、伤残最高100万，医疗10万0免赔，不限医保，公共交通意外叠加赔付 投保年龄：0-65周岁', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51200917_18_19_20/share_zongheyiwai.jpg'},
    {name: "众安家庭共享保额意外保险", productId: 1018178, title: '众安家庭共享保额意外保险', desc: '一人投保，全家共享百万保额,发生意外不再怕，医疗费、住院津贴、救护车费、自费药都保', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/travel/51331501_2_3/jiatinggongxiangbaoeyiwai_share.png'},
    {name: "众安女性尊享百万意外保险", productId: 1003072, title: '众安女性尊享百万意外保险', desc: '意外身故、伤残最高500万，猝死250万，可附加500万公共交通意外 投保年龄：18-60周岁', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51281044_5_6_7/share_nvxingyiwai.jpg'},
    {name: "众安孝欣保老年防癌保险（升级）", productId: 1021554, title: '众安孝欣保老年防癌保险（升级）', desc: '含质子重离子治疗及绿色通道服务！三高人群也可投保，最高可以续保到85岁！恶性肿瘤医疗保险金与质子重离子医疗保险金共享保额！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51337310_11_12_13/laonianfangaixian_share.jpg'},
    {name: "众安孝欣保老年综合意外险", productId: 1000197, title: '众安孝欣保老年综合意外险', desc: '意外身故、伤残最高20万，意外医疗2万0免赔，不限社保，公共交通意外叠加赔付；投保年龄：66-80周岁', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51255824_27/share_xiaoxinbao.jpg'},
    {name: "众安尊享e生旗舰版（升级）", productId: 1021043, title: '众安尊享e生旗舰版（升级）', desc: '保额600万，癌症无免赔，全家可共享免赔额，提供免费医疗垫付', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51335801_2_3_4/zunxiangyishengqijian_share.jpg'},
    {name: "众安尊享e生旗舰版", productId: 1012745, title: '众安尊享e生旗舰版', desc: '保额600万，癌症无免赔，全家可共享免赔额，提供免费医疗垫付', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/insurance/51321307_8/zunxiangyishengqijian_share.jpg'},
    {name: "中荷顾家保定期寿险", productId: 1010421, title: '中荷顾家保定期寿险', desc: '最高150万保额，最低每年63元起，保险期间灵活可选 投保年龄：18-45周岁 保障期限：10年/15年/20年/25年/30年 缴费期间：5/10/15/20/25年', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/120000001/iyb10004share.jpg'},
    {name: "中信保诚［荣耀祯爱］定期寿险", productId: 1019133, title: '中信保诚［荣耀祯爱］定期寿险', desc: '超短等待期，保障额度高 健告宽松，职业限制少', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/life/180000001/share.jpg'},
    {name: "团/安联邮轮旅行保险(碧海蓝天)", productId: 1024847},
    {name: "团/安联申根旅行保障计划(申根之王)", productId: 1024846},
    {name: "团/安心团体意外险", productId: 1013086},
    {name: "团/和谐建工意外险", productId: 1024732},
    {name: "团/平安乐享餐饮综合保险", productId: 1027058},
    {name: "团/平安团体意外险", productId: 1015236},
    {name: "团/史带“畅游华夏”境内旅行险", productId: 1017185},
    {name: "团/史带境内户外运动保险", productId: 1017187},
    {name: "团/史带境内拓展训练保险", productId: 1017186},
    {name: "团/史带“乐游全球”境外旅行险", productId: 1017188},
    {name: "团/太平建工意外险", productId: 1013087},
    {name: "团/众安雇主责任险", productId: 1012986},
    {name: "团/众安团体医疗险", productId: 1012984},
    {name: "团/众安团体意外险A(推广费固定)", productId: 1012976},
    {name: "团/众安团体意外险企惠保(推广费可调)", productId: 1012979},
    {name: "商/众安宫颈癌HPV基因检测", productId: 1023390, title: '宫颈癌HPV基因检测', desc: '荷兰原装进口，居家采样，全程隐私保密送检，优先预定4价疫苗，尊享宫颈医疗服务', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/service/370000008/gongjingai_share.jpg'},
    {name: "商/众安乳腺癌Brca1/2基因检测", productId: 1023389, title: '乳腺癌Brca1/2基因检测', desc: '科学检测基因 关爱乳腺健康 防范于未然 赠电话医生服务', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/service/370000007/ruxianai_share.jpg'},
    {name: "商/众安童安保儿童兴趣潜力基因检测", productId: 1021044, title: '童安保儿童兴趣潜力基因检测', desc: '检测孩子的空间定位能力、乐感、爆发力和耐力，科学获知孩子的体育、艺术等方面能力。检测结果高达99.99%！让孩子站在天赋的基石上，快乐成长，轻松成才！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/service/370000001/xingquqianli_share.png'},
    {name: "商/众安童安保安全用药基因检测", productId: 1021045, title: '童安保安全用药基因检测', desc: '覆盖内科、消化科、呼吸科等六大科室，涉及感冒、哮喘等11种常见儿童用药需求的基因检测。检测结果准确率高达99.99%！父母培养健康成长的权威指南！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/service/370000002/anquanyongyao_share.png'},
    {name: "商/众安童安保学习能力基因检测", productId: 1021046, title: '童安保学习能力基因检测', desc: '检测孩子的理解能力、数学计算能力、记忆力、求知欲、动手能力、阅读能力在内的6种学习能力。基因检测结果准确率高达99.99%！父母因材施教的权威指南！', imgUrl: 'https://static.zhongan.com/website/health/iybApp/upload/service/370000003/xuexinengli_share.png'},
];

ENV.products.map(x => {
    ENV.event.toProduct.comp[0].value.push({text: x.name, code: x.productId + ""});
    if (x.title)
        ENV.event.shareProduct.comp[0].value.push({text: x.name, code: x});
});

ENV.saveQueue = function() {
    if (Object.keys(ENV.ready).length <= 0)
        return;
    let ready = ENV.ready;
    ENV.ready = {};
    common.req("element.json", {
        actId: ENV.actId,
        elements: ready
    }, r => {
        // for (let x in ready)
        //     if (ENV.ready[x] == null)
        //         ENV.ready[x] = ready[x];
    });
};

ENV.getImage = function(url) {
    let img = ENV.images[url];
    if (img == null) {
        img = new Image();
        img.src = common.server() + "/" + url;
        img.onload = function() {
            img.succ = true;
            let listeners = ENV.imagesOnload[url];
            listeners.map(l => { l() });
        }
        img.onerror = function() {
        }
        ENV.imagesOnload[url] = [];
        ENV.images[url] = img;
    }
    return img;
}

ENV.drawImage = function(c, src, x, y, w, h, mode) {
    let img = ENV.getImage(src);
    if (img.complete && img.succ) {
        c.drawImage(img, 0, 0, img.width, img.height, x, y, w, h);
    } else {
        let ll = ENV.imagesOnload[src];
        ll.push(function() {
            c.drawImage(img, 0, 0, img.width, img.height, x, y, w, h);
        });
    }
}

ENV.draw = function(now) {
    let canvas = document.getElementById("canvas");
    let c = canvas.getContext("2d");
    c.fillStyle = "black";
    c.fillRect(0, 0, ENV.W, ENV.H);
    let page = ENV.doc.pages[ENV.index];
    if (page.background != null) {
        if (page.mode == 1) {
            ENV.drawImage(c, page.background, 0, 0, ENV.W, ENV.H);
        } else {
            ENV.drawImage(c, page.background, 0, 0, ENV.W, ENV.H, true);
        }
    }
    page.elements.map(e => {
        if (e.display == 0) return;
        ENV.drawElement(c, e);
        if (e.children != null) e.children.map(e => {
            ENV.drawElement(c, e);
            if (e.children != null) e.children.map(e => {
                ENV.drawElement(c, e);
            });
        });
    })
    if (now) {
        c.fillStyle = "rgba(100%,60%,60%,0.5)";
        c.fillRect(now.cx, now.cy, now.w, now.h);
    }
    if (ENV.cursorRect) {
        c.fillStyle = "rgba(100%,60%,60%,0.5)";
        c.fillRect(ENV.cursorRect.x, ENV.cursorRect.y, ENV.cursorRect.w, ENV.cursorRect.h);
    }
}

ENV.drawElement = function(c, e) {
    if (e.bgColor != null && e.bgColor != "") {
        c.fillStyle = ENV.color(e.bgColor);
        c.fillRect(e.cx, e.cy, e.w, e.h);
    }
    if (e.image != null && e.image.length > 0) {
        ENV.drawImage(c, e.image[0], e.cx, e.cy, e.w, e.h);
    }
}

ENV.color = function(c) {
    if (c.startsWith("rgb"))
        return c;
    else
        return "#" + c;
}

ENV.in = function(px, py, x, y, w, h) {
    return px > x && py > y && px < x + w && py < y + h;
}

ENV.inRC = function(x, y, e) {
    return ENV.in(x, y, e.cx, e.cy, e.w, e.h);
}

var Element = {
    setStyle: function(e, style) {
        if (e.style == null)
            e.style = {};
        if (e.style[style] == null)
            e.style[style] = "";
        else
            delete e.style[style];
        this.saveElement();
    },
    getStyle: function(e) {
        let r = "";
        if (e.style != null) for (let x in e.style) {
            if (e.style[x])
                r += x + ",";
        }
        return r;
    },
    addAction: function(e, action) {
        if (action == null) {
            e.action = [];
        } else {
            e.action.push({type:action});
        }
        this.saveElement();
    },
    onEventLink: function(e, ev) {
        ev.dataTransfer.setData("dragType", "event");
        ev.dataTransfer.setData("srcId", e.id);
    },
    onAdjust: function(e, ev) {
        ev.dataTransfer.setData("dragType", "element");
        ev.dataTransfer.setData("srcId", e.id);
    },
    toDivs: function(self, i, elements, parent) {
        return elements == null ? null : elements.map(e => {
            ENV.mapping[e.id] = parent;
            let text = "元素 <" + Math.round(e.x) + "," + Math.round(e.y) + "><" + Math.round(e.w) + "," + Math.round(e.h) + ">" + (e.name ? e.name : "");
            return (
                <div className="ml-3" key={e.id}>
                    <div className={"form-row pl-2 pr-2 pt-1 pb-1 " + (e == self.state.element ? "text-white bg-danger" : "")} onClick={self.select.bind(self, i, e)} draggable="true" onDragStart={this.onAdjust.bind(self, e)}>
                        <div data-drop="true" id={e.id} className="mr-auto">{text}</div>
                        <div onClick={self.display.bind(self, e)}>&nbsp;&nbsp;{e.display == 0?"☆":"★"}&nbsp;&nbsp;</div>
                        <div onClick={self.delete.bind(self, e.id)}>&nbsp;&nbsp;╳&nbsp;&nbsp;</div>
                    </div>
                    <div className="ml-3">
                        { e.events == null ? null : e.events.map(ev => {
                            return <div key={ev.id} className="form-row pl-2 pr-2 pt-1 pb-1" draggable="true" onDragStart={this.onEventLink.bind(self, ev)}>
                                <div data-drop="true" id={ev.id} className="mr-auto">事件 [{ev.type}]{ev.onFinish == null ? "" : " → " + ev.onFinish.type}</div>
                                <div onClick={self.deleteEvent.bind(self, e.id, ev.id)}>&nbsp;&nbsp;╳&nbsp;&nbsp;</div>
                            </div>
                        })}
                    </div>
                    { Element.toDivs(self, i, e.children, e) }
                </div>
            );
        });
    }
}

var Event = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
        this.setState({events:this.props.element.events});
    },
    add: function(type) {
        common.req("event.json", {
            actId: ENV.actId,
            elementId: this.props.element.id,
            type: type
        }, r => {
            this.props.parent.rebuild(r);
        });
    },
    delete(eventId) {
        if (confirm("删除？")) {
            common.req("del_event.json", {
                actId: ENV.actId,
                elementId: this.props.element.id,
                eventId: eventId
            }, r => {
                this.props.parent.rebuild(r);
            });
        }
    },
    save(ev) {
        common.req("save_event.json", {
            actId: ENV.actId,
            event: ev
        }, r => {
            this.props.parent.refresh(this.props.parent.state.element);
        });
    },
    render() {
        return <div>
            { this.state.events == null ? null : this.state.events.map(s => {
                let event = ENV.event[s.type];
                if (s.param == null) s.param = {};
                let comps = !event || !event.comp ? null : event.comp.map((c, i) => {
                    let comp = null;
                    if (c.type == "option") {
                        comp = <select className="form-control" value={s.param[c.code]} onChange={v => {
                            if (v.target.value != null && v.target.value != "") {
                                let val = JSON.parse(v.target.value);
                                for (let k in val)
                                    s.param[k] = val[k];
                                this.save(s);
                            }
                        }}>
                            {c.value.map(o => <option value={JSON.stringify(o.code)}>{o.text}</option>)}
                        </select>;
                    } else if (c.type == "select") {
                            comp = <select className="form-control" value={s.param[c.code]} onChange={v => { s.param[c.code] = v.target.value; this.save(s); }}>
                                { c.value.map(o => typeof o.code == "string" ? <option value={o.code}>{o.text}</option> : null) }
                            </select>;
                    } else if (c.type == "input") {
                        comp = <input className="form-control" value={s.param[c.code]} onChange={v => { s.param[c.code] = v.target.value; this.save(s); }}/>;
                    } else if (c.type == "switch") {
                        comp = <select className="form-control" value={s.param[c.code] ? "Y" : "N"} onChange={v => { s.param[c.code] = v.target.value == "Y"; this.save(s); }}>
                            <option value="N">否</option>
                            <option value="Y">是</option>
                        </select>;
                    }
                    return (
                        <div className="input-group pl-2 pr-2 pt-1 pb-1" key={i}>
                            <div className="input-group-prepend">
                                <div className="btn btn-primary" style={{width:"120px"}}>{c.label}</div>
                            </div>
                            {comp}
                        </div>
                    );
                });
                return (
                    <div className="card mt-2 mb-1" key={s.id}>
                        <div className="card-header input-group">
                            <div className="mr-auto">{s.type}</div>
                            <div onClick={this.delete.bind(this, s.id)}>X</div>
                        </div>
                        <div className="card-body">
                            {comps}
                            <div className="input-group pl-2 pr-2 pt-1 pb-1">
                                <div className="input-group-prepend">
                                    <div className="btn btn-primary" style={{width:"120px"}}>参数json</div>
                                </div>
                                <input type="text" className="form-control" ref="evOnFinish" value={s.param==null?null:JSON.stringify(s.param)} readOnly="true"/>
                            </div>
                            <div className="input-group pl-2 pr-2 pt-1 pb-1">
                                <div className="input-group-prepend">
                                    <div className="btn btn-primary" style={{width:"120px"}}>结束事件</div>
                                </div>
                                <input type="text" className="form-control" ref="evOnFinish" value={s.onFinish==null?null:JSON.stringify(s.onFinish)} readOnly="true"/>
                            </div>
                        </div>
                    </div>
                )
            })}
            <div className="input-group pl-2 pr-2 pt-1 pb-1">
                <button id="eEvent" className="ml-auto btn btn-success" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">★ 事件</button>
                <div className="dropdown-menu" aria-labelledby="eEvent">
                    { Object.entries(ENV.event).map(v => <a key={v[0]} className="dropdown-item" onClick={this.add.bind(this, v[0])}>{v[1].text}</a>) }
                </div>
            </div>
        </div>
    }
});

var Style = React.createClass({
    getInitialState() {
        return {css: {}};
    },
    componentDidMount() {
        this.setState({css: this.props.element.style});
    },
    delete(k) {
        let m = this.state.css;
        delete m[k];
        this.setState({css: m}, () => {
            this.props.element.style = m;
            this.save();
        });
    },
    save() {
        ENV.ready[this.props.element.id] = this.props.element;
        // common.req("style.json", {
        //     actId: ENV.actId,
        //     elementId: this.props.element.id,
        //     style: this.props.element.style
        // }, r => {});
    },
    add(type) {
        let p = this.state.css;
        p[type] = {};
        this.setState({css: p}, () => {
            this.props.element.style = p;
            this.save();
        });
    },
    render() {
        return <div>
            { Object.keys(this.state.css).map(s => {
                let style = ENV.style[s];
                let comps = !style || !style.comp ? null : style.comp.map(c => {
                    let p = this.state.css[s];
                    let func = v => {
                        let q = this.state.css;
                        p[c.code] = v.target.value;
                        q[s] = p;
                        this.setState({css: q}, () => {
                            this.props.element.style = this.state.css;
                            this.save();
                        });
                    };
                    let comp = null;
                    if (c.type == "select") {
                        comp = <select className="form-control" value={p[c.code]} onChange={func}>
                            { c.value.map(o => <option value={o.code}>{o.text}</option>) }
                        </select>;
                    } else if (c.type == "input") {
                        comp = <input className="form-control" value={p[c.code]} onBlur={func} onChange={func}/>;
                    }
                    return (
                        <div className="input-group pl-2 pr-2 pt-1 pb-1">
                            <div className="input-group-prepend">
                                <div className="btn btn-primary" style={{width:"120px"}}>{c.label}</div>
                            </div>
                            {comp}
                        </div>
                    );
                });
                return (
                    <div className="card mt-2 mb-1" id={"style" + s} key={s}>
                        <div className="card-header input-group">
                            <div className="mr-auto">{s}</div>
                            <div onClick={this.delete.bind(this, s)}>X</div>
                        </div>
                        <div className="card-body">
                            {comps}
                        </div>
                    </div>
                )
            })}
            <div className="input-group pl-2 pr-2 pt-1 pb-1">
                <button id="eStyle" className="ml-auto btn btn-success" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">★ 样式</button>
                <div className="dropdown-menu" aria-labelledby="eStyle">
                    { Object.entries(ENV.style).map(v => <a className="dropdown-item" onClick={this.add.bind(this, v[0])}>{v[1].text}</a>) }
                </div>
            </div>
        </div>
    }
});

var Main = React.createClass({
    getInitialState() {
        return {w:90, h:160, mode:1, element:null};
    },
    reload() {
        ENV.saveQueue();
        common.req("view_act.json", {actId:ENV.actId}, r => this.rebuild(r));
    },
    componentDidMount() {
        this.reload();
        let drop = e => {
            e.preventDefault();
            var fileList = e.dataTransfer.files;
            if(fileList.length > 0) {
                var xhr = new XMLHttpRequest();
                xhr.open("post", common.url("file.do"), true);
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhr.onreadystatechange = r => {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        this.rebuild(JSON.parse(xhr.responseText).content);
                    }
                };
                var fd = new FormData();
                fd.append("pageIndex", ENV.index);
                fd.append("actId", ENV.doc.actId);
                if (e.target.id == 'pBg') {
                    fd.append("type", "desk");
                } else if (e.target.id == 'eImage') {
                    fd.append("elementId", this.state.element.id);
                    fd.append("type", "image");
                } else if (e.target.id == 'canvas') {
                    let c = $("#canvas");
                    let s = c.offset();
                    let x = (e.clientX - s.left) * ENV.W / c.width();
                    let y = (e.clientY - s.top) * ENV.H / c.height();
                    fd.append("x", x);
                    fd.append("y", y);
                    if (this.state.element != null) {
                        if (ENV.inRC(x, y, this.state.element)) {
                            fd.append("x", x - this.state.element.cx);
                            fd.append("y", y - this.state.element.cy);
                            fd.append("parentId", this.state.element.id);
                        }
                    }
                    fd.append("type", "element");
                } else {
                    fd = null;
                }
                if (fd != null) {
                    for (var i = 0; i < fileList.length; i++)
                        fd.append("file", fileList[i]);
                    xhr.send(fd);
                }
            } else {
                if (!e.target.getAttribute("data-drop"))
                    return;
                e.target.style.border = ENV.mapping[e.target];
                let dstId = e.target.id;
                let srcId = e.dataTransfer.getData("srcId");
                let type = e.dataTransfer.getData("dragType");
                if (srcId != null && srcId != "" && dstId != null && dstId != "") {
                    if (type == "event") {
                        if (dstId == "eAction") {
                            this.state.element.action.push({type: "event", eventId: srcId})
                            this.saveElement();
                        } else {
                            this.link(srcId, dstId);
                        }
                    } else if (type == "element") {
                        if (dstId.startsWith("page")) {
                            this.adjust(srcId, dstId.substr(4), null);
                        } else {
                            this.adjust(srcId, null, dstId);
                        }
                    }
                }
            }
        };

        let dragEnter = e => {
            if (e.target.getAttribute("data-drop")) {
                ENV.mapping[e.target] = e.target.style.border;
                e.target.style.border = "3px dashed red";
            }
        }
        let dragLeave = e => {
            if (e.target.getAttribute("data-drop"))
                e.target.style.border = ENV.mapping[e.target];
        }

        document.getElementById('content').addEventListener("drop", drop);
        document.getElementById('content').addEventListener("dragenter", dragEnter);
        document.getElementById('content').addEventListener("dragleave", dragLeave);

        $(document).on({
            keydown: this.onKeyDown,
            dragleave:function(e){    //拖离
                e.preventDefault();
            },
            drop:function(e){  //拖后放
                e.preventDefault();
            },
            dragenter:function(e){    //拖进
                e.preventDefault();
            },
            dragover:function(e){    //拖来拖去
                e.preventDefault();
            }
        });
    },
    display: function(e) {
        if (e.display == 1)
            e.display = 0;
        else
            e.display = 1;
        this.saveElement();
    },
    adjust(e1, page, e2) {
        common.req("adjust_element.json", {
            actId: ENV.actId,
            page: page,
            elementId: e1,
            parentId: e2
        }, r => {
            this.rebuild(r);
        });
    },
    link(e1, e2) {
        common.req("link_event.json", {
            actId: ENV.actId,
            fromEventId: e2,
            invokeEventId: e1
        }, r => {
            this.rebuild(r);
        });
    },
    deleteEvent(elementId, eventId) {
        if (confirm("删除？")) {
            common.req("del_event.json", {
                actId: ENV.actId,
                elementId: elementId,
                eventId: eventId
            }, r => {
                this.rebuild(r);
            });
        }
    },
    find(page, element) {
        if (element == null)
            return null;
        let current = null;
        let func = e => {
            if (e.id == element.id) {
                current = e;
            } else if (e.children != null) {
                e.children.map(func);
            }
        }
        page.elements.map(func);
        return current;
    },
    rebuild(doc) {
        if (doc != null) {
            ENV.counter++;
            ENV.doc = doc;
            let page = ENV.doc.pages[ENV.index];
            ENV.W = page.w;
            ENV.H = page.h;
            let w = document.getElementById('desk').clientWidth;
            let current = this.find(page, this.state.element);
            this.setState({w: w, h: ENV.H * w / ENV.W, element: current}, () => { ENV.draw(current) })
        }
    },
    refresh(element) {
        this.setState({element: element}, () => { ENV.draw(this.state.element) })
    },
    clickSelect(e) {
        let c = $("#canvas");
        let s = c.offset();
        let x = (e.clientX - s.left) * ENV.W / c.width();
        let y = (e.clientY - s.top) * ENV.H / c.height();
        let page = ENV.doc.pages[ENV.index];
        let element;
        page.elements.map((e, i) => {
            if (ENV.inRC(x, y, e))
                element = e;
            if (e.children != null) e.children.map(e => {
                if (ENV.inRC(x, y, e))
                    element = e;
                if (e.children != null) e.children.map(e => {
                    if (ENV.inRC(x, y, e))
                        element = e;
                });
            });
        });
        if (element != null)
            this.refresh(element);
    },
    selectPage(i) {
        ENV.index = i;
        this.refresh();
    },
    select(i, e) {
        ENV.index = i;
        this.refresh(e);
    },
    selectById(eId) {
        let page = ENV.doc.pages[ENV.index];
        page.elements.map((e) => {
            if (e.id == eId) {
                this.refresh(e);
            } else if (e.children != null) e.children.map(e => {
                if (e.id == eId)
                    this.refresh(e);
            });
        });
    },
    drag(e) {
        let f = this.state.element;
        if (f == null)
            f = {};
        let c = $("#canvas");
        let s = c.offset();
        let x = (e.clientX - s.left) * ENV.W / c.width();
        let y = (e.clientY - s.top) * ENV.H / c.height();
        if (e.type == "dragstart") {
            ENV.drag = {sx:x, sy:y, ex:f.x, ey:f.y, cx:f.cx, cy:f.cy, ew:f.w, eh:f.h};
            ENV.cursorRect = null;
            if (e.altKey || e.ctrlKey) {
                ENV.drag.mode = 10;
            } else if (ENV.inRC(x, y, f)) {
                ENV.drag.mode = 5;
            } else if (ENV.in(x, y, f.cx - 20, f.cy - 20, 20, 20)) {
                ENV.drag.mode = 7;
            } else if (ENV.in(x, y, f.cx, f.cy - 20, f.w, 20)) {
                ENV.drag.mode = 8;
            } else if (ENV.in(x, y, f.cx + f.w, f.cy - 20, 20, 20)) {
                ENV.drag.mode = 9;
            } else if (ENV.in(x, y, f.cx - 20, f.cy, 20, f.h)) {
                ENV.drag.mode = 4;
            } else if (ENV.in(x, y, f.cx + f.w, f.cy, 20, f.h)) {
                ENV.drag.mode = 6;
            } else if (ENV.in(x, y, f.cx - 20, f.cy + f.h, 20, 20)) {
                ENV.drag.mode = 1;
            } else if (ENV.in(x, y, f.cx, f.cy + f.h, f.w, 20)) {
                ENV.drag.mode = 2;
            } else if (ENV.in(x, y, f.cx + f.w, f.cy + f.h, 20, 20)) {
                ENV.drag.mode = 3;
            }
        } else if (ENV.drag.mode != null) {
            if (e.type == "drag") {
                if (ENV.drag.mode == 10) {
                    ENV.cursorRect = {x:ENV.drag.sx, y:ENV.drag.sy, z:1, w:x - ENV.drag.sx, h:y - ENV.drag.sy};
                } else if (ENV.drag.mode == 5) {
                    this.state.element.x = ENV.drag.ex + x - ENV.drag.sx;
                    this.state.element.y = ENV.drag.ey + y - ENV.drag.sy;
                    this.state.element.cx = ENV.drag.cx + x - ENV.drag.sx;
                    this.state.element.cy = ENV.drag.cy + y - ENV.drag.sy;
                } else if (ENV.drag.mode == 7) {
                    this.state.element.x = ENV.drag.ex + x - ENV.drag.sx;
                    this.state.element.y = ENV.drag.ey + y - ENV.drag.sy;
                    this.state.element.cx = ENV.drag.cx + x - ENV.drag.sx;
                    this.state.element.cy = ENV.drag.cy + y - ENV.drag.sy;
                    this.state.element.w = ENV.drag.ew + ENV.drag.sx - x;
                    this.state.element.h = ENV.drag.eh + ENV.drag.sy - y;
                } else if (ENV.drag.mode == 8) {
                    this.state.element.y = ENV.drag.ey + y - ENV.drag.sy;
                    this.state.element.cy = ENV.drag.cy + y - ENV.drag.sy;
                    this.state.element.h = ENV.drag.eh + ENV.drag.sy - y;
                } else if (ENV.drag.mode == 9) {
                    this.state.element.y = ENV.drag.ey + y - ENV.drag.sy;
                    this.state.element.cy = ENV.drag.cy + y - ENV.drag.sy;
                    this.state.element.w = ENV.drag.ew + x - ENV.drag.sx;
                    this.state.element.h = ENV.drag.eh + ENV.drag.sy - y;
                } else if (ENV.drag.mode == 4) {
                    this.state.element.x = ENV.drag.ex + x - ENV.drag.sx;
                    this.state.element.cx = ENV.drag.cx + x - ENV.drag.sx;
                    this.state.element.w = ENV.drag.ew + ENV.drag.sx - x;
                } else if (ENV.drag.mode == 6) {
                    this.state.element.w = ENV.drag.ew + x - ENV.drag.sx;
                } else if (ENV.drag.mode == 1) {
                    this.state.element.x = ENV.drag.ex + x - ENV.drag.sx;
                    this.state.element.cx = ENV.drag.cx + x - ENV.drag.sx;
                    this.state.element.w = ENV.drag.ew + ENV.drag.sx - x;
                    this.state.element.h = ENV.drag.eh + y - ENV.drag.sy;
                } else if (ENV.drag.mode == 2) {
                    this.state.element.y = ENV.drag.ey + y - ENV.drag.sy;
                    this.state.element.cy = ENV.drag.cy + y - ENV.drag.sy;
                    this.state.element.h = ENV.drag.eh + y - ENV.drag.sy;
                } else if (ENV.drag.mode == 3) {
                    this.state.element.w = ENV.drag.ew + x - ENV.drag.sx;
                    this.state.element.h = ENV.drag.eh + y - ENV.drag.sy;
                }
                ENV.draw(this.state.element);
            } else if (e.type == "dragend") {
                if (ENV.drag.mode == 10) {
                    if (this.state.element != null && ENV.in(ENV.drag.sx, ENV.drag.sy, this.state.element.x, this.state.element.y, this.state.element.w, this.state.element.h)) {
                        ENV.cursorRect.parentId = this.state.element.id;
                        ENV.cursorRect.x = ENV.cursorRect.x - this.state.element.cx;
                        ENV.cursorRect.y = ENV.cursorRect.y - this.state.element.cy;
                    }
                    this.newElement(ENV.cursorRect);
                    ENV.cursorRect = null;
                } else if (this.state.element != null && ENV.drag.mode != null) {
                    this.state.element.hs = 0;
                    this.state.element.ys = 0;
                    this.saveElement();
                }
            }
        }
    },
    saveDoc() {
        common.req("save_act.json", {
            actId: ENV.actId,
            code: ENV.doc.code,
            name: ENV.doc.name,
            onStart: ENV.doc.startJs
        }, r => {
            this.rebuild(r);
        });
    },
    newPage() {
        common.req("new_page.json", {actId: ENV.actId}, r => {
            this.rebuild(r);
        });
    },
    copyPage() {
        common.req("copy_page.json", {actId: ENV.actId, pageIndex: ENV.index}, r => {
            this.rebuild(r);
        });
    },
    deletePage(i) {
        common.req("delete_page.json", {actId: ENV.actId, pageIndex: i}, r => {
            this.rebuild(r);
        });
    },
    savePage() {
        common.req("save_page.json", {
            actId: ENV.actId,
            pageIndex: ENV.index,
            page: ENV.doc.pages[ENV.index]
        }, r => {
            this.rebuild(r);
        });
    },
    newElement(element) {
        common.req("new_element.json", {
            actId: ENV.actId,
            pageIndex: ENV.index,
            element: element
        }, r => {
            this.rebuild(r);
        });
    },
    saveElement() {
        if (this.state.element != null) {
            this.refresh(this.state.element);
            ENV.ready[this.state.element.id] = this.state.element;
        }
    },
    delete(elementId) {
        if (confirm("删除？")) {
            common.req("del_element.json", {
                actId: ENV.actId,
                elementId: elementId
            }, r => {
                this.rebuild(r);
            });
        }
    },
    fillBgColor(e) { //以element的四个角的颜色平均值填充背景色
        let img = ENV.getImage(ENV.doc.pages[ENV.index].background);
        let canvas = document.getElementById("canvas");
        let c = canvas.getContext("2d");

        let x1 = e.x - 1;
        let y1 = e.y - 1;
        let x2 = e.x + e.w + 1;
        let y2 = e.y + e.h + 1;

        let rgb = [0, 0, 0, 0];
        if (x1 >= 0 && y1 >= 0 && x1 < ENV.W && y1 < ENV.H) {
            let d1 = c.getImageData(x1, y1, 1, 1).data;
            rgb[0] += d1[0];
            rgb[1] += d1[1];
            rgb[2] += d1[2];
            rgb[3]++;
        }
        if (x1 >= 0 && y2 >= 0 && x1 < ENV.W && y2 < ENV.H) {
            let d1 = c.getImageData(x1, y2, 1, 1).data;
            rgb[0] += d1[0];
            rgb[1] += d1[1];
            rgb[2] += d1[2];
            rgb[3]++;
        }
        if (x2 >= 0 && y1 >= 0 && x2 < ENV.W && y1 < ENV.H) {
            let d1 = c.getImageData(x2, y1, 1, 1).data;
            rgb[0] += d1[0];
            rgb[1] += d1[1];
            rgb[2] += d1[2];
            rgb[3]++;
        }
        if (x2 >= 0 && y2 >= 0 && x2 < ENV.W && y2 < ENV.H) {
            let d1 = c.getImageData(x2, y2, 1, 1).data;
            rgb[0] += d1[0];
            rgb[1] += d1[1];
            rgb[2] += d1[2];
            rgb[3]++;
        }
        if (rgb[3] > 0) {
            e.bgColor = "#" + (Math.round(rgb[0] / rgb[3])*65536 + Math.round(rgb[1] / rgb[3])*256 + Math.round(rgb[2] / rgb[3])).toString(16);
        }
        this.saveElement();
    },
    removeBg(e) {
        e.image = [];
        this.saveElement();
    },
    cutBg(e) {
        common.req("cut_bg.json", {actId: ENV.actId, elementId:e.id}, r => {
            this.refresh(this.state.element);
        });
    },
    setZIndex(e, k) {
        if (k == 0) {
            e.z = 0;
        } else {
            e.z += k;
        }
        this.saveElement();
    },
    deploy(env) {
        common.req("deploy.json", {actId: ENV.actId, env:env}, r => {
            console.log(r);
            alert(r);
        });
    },
    look(env) {
        window.open("./act/" + ENV.actId + "/" + env + ".html");
    },
    setDetailMode(mode) {
        this.setState({mode:mode});
    },
    onKeyDown(e) {
        if ((e.ctrlKey || e.altKey) && (e.keyCode >= 37 && e.keyCode <= 40)){
            this.state.element.hs = 0;
            this.state.element.ys = 0;
            if (e.keyCode == 37) {
                this.state.element.cx--;
                this.state.element.x--;
            } else if (e.keyCode == 38) {
                this.state.element.cy--;
                this.state.element.y--;
            } else if (e.keyCode == 39) {
                this.state.element.cx++;
                this.state.element.x++;
            } else if (e.keyCode == 40) {
                this.state.element.cy++;
                this.state.element.y++;
            }
            this.state.element.x = Math.round(this.state.element.x);
            this.state.element.y = Math.round(this.state.element.y);
            this.state.element.cx = Math.round(this.state.element.cx);
            this.state.element.cy = Math.round(this.state.element.cy);
            this.saveElement();
        }
    },
    submitTest() {
        if (confirm("确定提交？")) common.req("submit_test.json", {actId: ENV.actId, address: "guye@iyunbao.com,caojianxiang@iyunbao.com,zhangqiliang@iyunbao.com,dingliang@iyunbao.com,xuechuangwei@iyunbao.com,qinyang@iyunbao.com,baoting@iyunbao.com,lixinhao@iyunbao.com"}, r => {
            alert(r);
        });
    },
    render() {
        let tree = ENV.doc.pages.map((page, i) => {
            return (
                <div className="ml-3" key={i}>
                    <div data-drop="true" id={"page"+i} className={"form-row pl-2 pr-2 pt-1 pb-1 " + (this.state.element == null && i == ENV.index ? "text-white bg-danger" : "")} onClick={this.selectPage.bind(this, i)}>
                        <div className="mr-auto">第{i+1}页</div>
                        <div onClick={this.deletePage.bind(this, i)}>&nbsp;&nbsp;╳&nbsp;&nbsp;</div>
                    </div>
                    { Element.toDivs(this, i, page.elements) }
                </div>
            )
        });
        let detail;
        if (this.state.element == null && ENV.doc.pages.length > ENV.index) {
            let p = ENV.doc.pages[ENV.index];
            detail = <div key={"page" + ENV.index}>
                <div className="input-group pl-2 pr-2 pt-1 pb-1">
                    <div className="input-group-prepend">
                        <div className="btn btn-primary" style={{width:"120px"}}>活动CODE</div>
                    </div>
                    <input type="text" className="form-control" ref="actCode" defaultValue={ENV.doc.code} onChange={v => {ENV.doc.code = v.target.value}} onBlur={this.saveDoc}/>
                </div>
                <div className="input-group pl-2 pr-2 pt-1 pb-1">
                    <div className="input-group-prepend">
                        <div className="btn btn-primary" style={{width:"120px"}}>活动名</div>
                    </div>
                    <input type="text" className="form-control" ref="actName" defaultValue={ENV.doc.name} onChange={v => {ENV.doc.name = v.target.value}} onBlur={this.saveDoc}/>
                </div>
                <div className="input-group pl-2 pr-2 pt-1 pb-1">
                    <div className="input-group-prepend">
                        <div className="btn btn-primary" style={{width:"120px"}}>启动JS</div>
                    </div>
                    <input type="text" className="form-control" ref="actName" defaultValue={ENV.doc.startJs} onChange={v => {ENV.doc.startJs = v.target.value}} onBlur={this.saveDoc}/>
                </div>
                <br/>
                <div className="input-group pl-2 pr-2 pt-1 pb-1">
                    <div className="input-group-prepend">
                        <div className="btn btn-primary" style={{width:"120px"}}>宽</div>
                    </div>
                    <input type="text" className="form-control" ref="pW" defaultValue={p.w} onChange={v => {p.w = v.target.value}} onBlur={this.savePage}/>
                </div>
                <div className="input-group pl-2 pr-2 pt-1 pb-1">
                    <div className="input-group-prepend">
                        <div className="btn btn-primary" style={{width:"120px"}}>高</div>
                    </div>
                    <input type="text" className="form-control" readOnly={p.mode == 2} ref="pH" defaultValue={p.h} onChange={v => {p.h = v.target.value}} onBlur={this.savePage}/>
                    <div className="input-group-append">
                        <div className="btn btn-outline-primary" onClick={v => { p.mode = 2; this.savePage(); }}>满屏</div>
                    </div>
                </div>
                <div className="input-group pl-2 pr-2 pt-1 pb-1">
                    <div className="input-group-prepend">
                        <div className="btn btn-primary" style={{width:"120px"}}>背景</div>
                    </div>
                    <input type="text" className="form-control" id="pBg" ref="pBg" value={p.background} readOnly="true"/>
                    <div className="input-group-append">
                        <button id="eBgStyle" className="ml-auto btn btn-outline-primary" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">样式</button>
                        <div className="dropdown-menu" aria-labelledby="eBgStyle">
                            <a className="dropdown-item" onClick={v => { p.bgMode = 1; this.savePage(); }}>横撑满</a>
                            <a className="dropdown-item" onClick={v => { p.bgMode = 3; this.savePage(); }}>重复</a>
                        </div>
                    </div>
                </div>
            </div>
        } else if (this.state.element != null) {
            let e = this.state.element;
            let divs;
            if (this.state.mode == 1) {
                divs = <div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>名称</div>
                        </div>
                        <input type="text" className="form-control" ref="eName" defaultValue={e.name} onChange={v => { e.name = v.target.value; this.saveElement(); }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>X</div>
                        </div>
                        <input type="text" className="form-control" ref="eX" defaultValue={e.x} onChange={v => { e.x = v.target.value; this.saveElement(); }}/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={v => { e.x = ((ENV.mapping[e.id] ? ENV.mapping[e.id].w : ENV.W) - e.w) / 2; this.saveElement(); }}>居中</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>Y</div>
                        </div>
                        <input type="text" className="form-control" ref="eY" defaultValue={e.ys == 1 ? "居中" : e.ys == 2 ? "置底" : e.y} onChange={ v => {e.ys = 0; e.y = v.target.value; this.saveElement(); }} onFocus={v => {this.refs.eY.value = e.y}}/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={v => {e.ys = 1; this.saveElement();}}>居中</div>
                            <div className="btn btn-outline-primary" onClick={v => {e.ys = 2; this.saveElement();}}>置底</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>Z</div>
                        </div>
                        <input type="text" className="form-control text-center" ref="eZ" value={e.z} readOnly="true"/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={this.setZIndex.bind(this, e, 1)}>↑</div>
                            <div className="btn btn-outline-primary" onClick={this.setZIndex.bind(this, e, -1)}>↓</div>
                            <div className="btn btn-outline-primary" onClick={this.setZIndex.bind(this, e, 0)}>RESET</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>宽</div>
                        </div>
                        <input type="text" className="form-control" ref="eW" defaultValue={e.w} onChange={v => {e.w = v.target.value; this.saveElement(); }}/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={v => {e.w = 750; this.refs.eW.value = e.w; this.saveElement(); }}>满屏</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>高</div>
                        </div>
                        <input type="text" className="form-control" ref="eH" defaultValue={e.hs == 1 ? "满屏" : e.h} onChange={v => {e.hs = 0; e.h = v.target.value; this.saveElement(); }} onFocus={v => {this.refs.eH.value = e.h}}/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={v => {e.hs = 1; this.saveElement();}}>满屏</div>
                        </div> 
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>纵横比</div>
                        </div>
                        <select className="form-control" ref="eKeep">
                            <option value="Y">自由</option>
                            <option value="N">按图片锁定</option>
                        </select>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>背景色</div>
                        </div>
                        <input type="text" className="form-control" id="eBgColor" ref="eBgColor" defaultValue={e.bgColor} onChange={v => {e.bgColor = v.target.value; this.saveElement(); }}/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={this.fillBgColor.bind(this, e)}>同色填充</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>颜色</div>
                        </div>
                        <input type="text" className="form-control" id="eColor" ref="eColor" defaultValue={e.color} onChange={v => {e.color = v.target.value; this.saveElement(); }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>文字</div>
                        </div>
                        <select className="form-control" ref="eFontSize" defaultValue={e.fontSize} onChange={v => {e.fontSize = v.target.value; this.saveElement(); }}>
                            <option value="0">无</option>
                            <option value="7">7px</option>
                            <option value="8">8px</option>
                            <option value="9">9px</option>
                            <option value="10">10px</option>
                            <option value="11">11px</option>
                            <option value="12">12px</option>
                            <option value="14">14px</option>
                            <option value="16">16px</option>
                            <option value="18">18px</option>
                            <option value="20">20px</option>
                            <option value="22">22px</option>
                            <option value="24">24px</option>
                            <option value="28">28px</option>
                            <option value="32">32px</option>
                            <option value="48">48px</option>
                            <option value="64">64px</option>
                            <option value="72">72px</option>
                        </select>
                        <div className="input-group-append">
                            <select className="form-control" ref="eLineHeight" defaultValue={e.lineHeight} onChange={v => { e.lineHeight = v.target.value; this.saveElement(); }}>
                                <option value="0">无</option>
                                <option value="10">10px</option>
                                <option value="16">16px</option>
                                <option value="20">20px</option>
                                <option value="24">24px</option>
                                <option value="30">30px</option>
                                <option value="30">36px</option>
                                <option value="40">40px</option>
                                <option value="50">50px</option>
                                <option value="60">60px</option>
                                <option value="70">70px</option>
                                <option value="80">80px</option>
                                <option value="90">90px</option>
                                <option value="100">100px</option>
                            </select>
                            <select className="form-control" ref="eAlign" defaultValue={e.align} onChange={v => { e.align = v.target.value; this.saveElement(); }}>
                                <option value="5">居中</option>
                                <option value="1">左下</option>
                                <option value="2">下</option>
                                <option value="3">右下</option>
                                <option value="4">左</option>
                                <option value="6">右</option>
                                <option value="7">左上</option>
                                <option value="8">上</option>
                                <option value="9">右上</option>
                            </select>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>文本</div>
                        </div>
                        <input type="text" className="form-control" id="eText" ref="eText" defaultValue={e.text} onChange={v => {e.text = v.target.value; this.saveElement(); }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>视频</div>
                        </div>
                        <input type="text" className="form-control" id="eVideo" ref="eVideo" defaultValue={e.video} onChange={v => {e.video = v.target.value; this.saveElement(); }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>背景</div>
                        </div>
                        <input type="text" className="form-control" id="eImage" ref="eImage" value={JSON.stringify(e.image)} readOnly="true"/>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={this.removeBg.bind(this, e)}>清除</div>
                            <div className="btn btn-outline-primary" onClick={this.cutBg.bind(this, e)}>截取</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>onClick</div>
                        </div>
                        <div className="form-control p-0 m-0 border-0">
                            <input data-drop="true" className="form-control" id="eAction" ref="eAction" readOnly="true" value={JSON.stringify(e.action)}/>
                        </div>
                        <div className="input-group-append">
                            <div className="btn btn-outline-primary" onClick={Element.addAction.bind(this, e, null)}>清空</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>输入框</div>
                        </div>
                        <input type="text" className="form-control" id="eInput" ref="eInput" defaultValue={e.input} onChange={v => {e.input = v.target.value; this.saveElement(); }}/>
                        <div className="input-group-append">
                            <div className={e.inputVerify && e.inputVerify.require ? "btn btn-primary" : "btn btn-outline-primary"} onClick={() => {
                                if (e.inputVerify == null)
                                    e.inputVerify = {};
                                e.inputVerify.require = !e.inputVerify.require;
                                this.saveElement();
                            }}>非空</div>
                        </div>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>可见表达式</div>
                        </div>
                        <input type="text" className="form-control" ref="eVisible" defaultValue={e.visible} onChange={v => { e.visible = v.target.value; this.saveElement(); }}/>
                    </div>
                    <div className="input-group pl-2 pr-2 pt-1 pb-1">
                        <div className="input-group-prepend">
                            <div className="btn btn-primary" style={{width:"120px"}}>循环表达式</div>
                        </div>
                        <input type="text" className="form-control" ref="eList" defaultValue={e.list} onChange={v => { e.list = v.target.value; this.saveElement(); }}/>
                    </div>
                </div>
            } else if (this.state.mode == 2) {
                divs = <Event key={ENV.counter} parent={this} element={e}/>;
            } else if (this.state.mode == 3) {
                divs = <Style key={ENV.counter} element={e}/>;
            }
            detail = <div key={e.id}>
                <div className="input-group pl-2 pr-2 pt-1 pb-1 text-center">
                    <div className="btn-group ml-auto mr-auto">
                        <button type="button" className={"btn " + (this.state.mode == 1 ? "btn-success" : "btn-outline-success")} onClick={this.setDetailMode.bind(this, 1)}>&nbsp;&nbsp;组件&nbsp;&nbsp;</button>
                        <button type="button" className={"btn " + (this.state.mode == 2 ? "btn-success" : "btn-outline-success")} onClick={this.setDetailMode.bind(this, 2)}>&nbsp;&nbsp;事项&nbsp;&nbsp;</button>
                        <button type="button" className={"btn " + (this.state.mode == 3 ? "btn-success" : "btn-outline-success")} onClick={this.setDetailMode.bind(this, 3)}>&nbsp;&nbsp;样式&nbsp;&nbsp;</button>
                    </div>
                </div>
                {divs}
            </div>
        }
        return (
            <div className="form-horizontal">
                <div className="form-row m-0 p-0">
                    <div className="col-sm-4 m-0 p-0" id="desk" style={{overflowY:"scroll", overflowX:"hidden", height:window.innerHeight + "px"}}>
                        <canvas id="canvas" ref="canvas" style={{width:this.state.w + "px", height:this.state.h + "px"}} width={ENV.W} height={ENV.H} onClick={this.clickSelect} draggable="true" onDragStart={this.drag} onDrag={this.drag} onDragEnd={this.drag}></canvas>
                    </div>
                    <div className="col-sm-8 m-0 p-0">
                        <div className="navbar navbar-expand-lg navbar-light bg-light m-0 p-3" style={{height:"60px"}}>
                            <div className="mr-auto">
                                <button type="button" className="btn btn-success mr-2" onClick={this.reload}>刷新界面</button>
                                <button type="button" className="btn btn-success mr-2" onClick={this.newPage}>新增页面</button>
                                <button type="button" className="btn btn-success mr-2" onClick={this.copyPage}>复制页面</button>
                            </div>
                            <div className="text-right">
                                <button type="button" className="btn btn-success mr-2" onClick={this.look.bind(this, "test")}>演示测试</button>
                                <button type="button" className="btn btn-success mr-2" onClick={this.look.bind(this, "uat")}>演示预发</button>
                                <button type="button" className="btn btn-success mr-2" onClick={this.look.bind(this, "prd")}>演示生产</button>
                                <button type="button" className="btn btn-success mr-2" onClick={this.submitTest}>提交测试</button>
                                <button type="button" className="btn btn-danger ml-2" onClick={this.deploy.bind(this, "test")}>发布测试</button>
                                <button type="button" className="btn btn-danger ml-2" onClick={this.deploy.bind(this, "uat")}>发布预发</button>
                                <button type="button" className="btn btn-danger ml-2" onClick={this.deploy.bind(this, "prd")}>发布生产</button>
                            </div>
                        </div>
                        <div className="form-row m-0 p-0">
                            <div className="col-sm-6 p-0 m-0" style={{overflowY:"scroll", overflowX:"hidden", height:window.innerHeight - 60 + "px"}}>
                                <div className="pl-2 pr-2 pt-1 pb-1">活动文档</div>
                                {tree}
                            </div>
                            <div className="col-sm-6 p-3" id="detail">
                                {detail}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ENV.actId = common.param("actId");

    if (ENV.actId == null || ENV.actId == "")
        common.req("new_act.json", {}, r => { document.location.href = "./draw.html?actId=" + r.actId });
    else
        ReactDOM.render(<Main/>, document.getElementById("content"));

    setInterval(ENV.saveQueue, 10000);
});
