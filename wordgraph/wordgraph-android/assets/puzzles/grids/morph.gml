Creator	"yFiles"
Version	"2.11"
graph
[
	hierarchic	1
	label	""
	directed	1
	node
	[
		id	0
		label	""
		graphics
		[
			x	240.0
			y	250.0
			w	480.0
			h	500.0
			type	"rectangle"
			fill	"#FFCC00"
			outline	"#000000"
		]
		LabelGraphics
		[
		]
	]
	node
	[
		id	1
		label	"BHx"
		graphics
		[
			x	379.79999999999995
			y	220.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"BHx"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	2
		label	"A"
		graphics
		[
			x	379.79999999999995
			y	60.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"A"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	3
		label	"N"
		graphics
		[
			x	210.0
			y	60.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"N"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	4
		label	"C"
		graphics
		[
			x	40.200000000000045
			y	60.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"C"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	5
		label	"O"
		graphics
		[
			x	380.20000000000005
			y	380.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"O"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	6
		label	"U"
		graphics
		[
			x	210.0
			y	380.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"U"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	7
		label	"T"
		graphics
		[
			x	40.200000000000045
			y	380.0
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"T"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	8
		label	"E"
		graphics
		[
			x	40.200000000000045
			y	225.272
			w	60.0
			h	60.0
			type	"rectangle"
			fill	"#99CCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"E"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	edge
	[
		source	1
		target	2
		graphics
		[
			fill	"#000000"
			Line
			[
				point
				[
					x	379.79999999999995
					y	220.0
				]
				point
				[
					x	379.79999999999995
					y	372.5
				]
				point
				[
					x	379.79999999999995
					y	372.5
				]
				point
				[
					x	379.79999999999995
					y	60.0
				]
			]
		]
	]
	edge
	[
		source	2
		target	3
		graphics
		[
			fill	"#000000"
		]
	]
	edge
	[
		source	3
		target	4
		graphics
		[
			fill	"#000000"
		]
	]
	edge
	[
		source	1
		target	5
		label	"BO2"
		graphics
		[
			fill	"#000000"
		]
		edgeAnchor
		[
			xSource	0.5058933333333357
			ySource	0.46479999999999866
			xTarget	0.49255999999999933
			yTarget	-0.5802693333333461
		]
		LabelGraphics
		[
			text	"BO2"
			fontSize	12
			fontName	"Dialog"
			configuration	"AutoFlippingLabel"
			contentWidth	28.01171875
			contentHeight	18.701171875
			model	"six_pos"
			position	"tail"
		]
	]
	edge
	[
		source	5
		target	6
		graphics
		[
			fill	"#000000"
		]
	]
	edge
	[
		source	6
		target	7
		graphics
		[
			fill	"#000000"
		]
		edgeAnchor
		[
			ySource	-0.25
			yTarget	-0.25
		]
	]
	edge
	[
		source	1
		target	5
		label	"BO1"
		graphics
		[
			fill	"#000000"
		]
		edgeAnchor
		[
			xSource	-0.4769919999999994
			ySource	0.33279733333332234
			xTarget	-0.4903253333333358
			yTarget	-0.47786933333334597
		]
		LabelGraphics
		[
			text	"BO1"
			fontSize	12
			fontName	"Dialog"
			configuration	"AutoFlippingLabel"
			contentWidth	28.01171875
			contentHeight	18.701171875
			model	"six_pos"
			position	"ttail"
		]
	]
	edge
	[
		source	5
		target	7
		graphics
		[
			fill	"#000000"
		]
	]
	edge
	[
		source	7
		target	7
		graphics
		[
			fill	"#000000"
			Line
			[
				point
				[
					x	40.200000000000045
					y	380.0
				]
				point
				[
					x	24.90479999999996
					y	435.048
				]
				point
				[
					x	90.62400000000008
					y	435.048
				]
				point
				[
					x	90.62400000000008
					y	391.12559999999985
				]
				point
				[
					x	40.200000000000045
					y	380.0
				]
			]
		]
		edgeAnchor
		[
			xSource	-0.5098400000000028
			ySource	0.41351999999999406
			xTarget	0.49282666666666347
			yTarget	0.3708533333333283
		]
	]
	edge
	[
		source	7
		target	8
		graphics
		[
			fill	"#000000"
		]
	]
]
