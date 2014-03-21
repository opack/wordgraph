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
			x	40.200000000000045
			y	435.0
			w	64.0
			h	64.0
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
			x	40.200000000000045
			y	310.0
			w	64.0
			h	64.0
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
			x	40.200000000000045
			y	185.0
			w	64.0
			h	64.0
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
			w	64.0
			h	64.0
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
			x	165.20000000000005
			y	435.0
			w	64.0
			h	64.0
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
			x	290.20000000000005
			y	435.0
			w	64.0
			h	64.0
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
			x	290.20000000000005
			y	310.0
			w	64.0
			h	64.0
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
			x	440.20000000000005
			y	310.0
			w	64.0
			h	64.0
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
			xSource	0.307875000000003
			ySource	0.43574999999999875
			xTarget	0.08162500000000339
			yTarget	0.43574999999999875
		]
		LabelGraphics
		[
			text	"BO2"
			fontSize	12
			fontName	"Dialog"
			configuration	"AutoFlippingLabel"
			contentWidth	28.01171875
			contentHeight	18.701171875
			model	"null"
			position	"null"
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
			xSource	0.49987500000000296
			ySource	-0.46025000000000027
			xTarget	-0.1423749999999968
			yTarget	-0.46025000000000027
		]
		LabelGraphics
		[
			text	"BO1"
			fontSize	12
			fontName	"Dialog"
			configuration	"AutoFlippingLabel"
			contentWidth	28.01171875
			contentHeight	18.701171875
			model	"null"
			position	"null"
		]
	]
	edge
	[
		source	5
		target	7
		graphics
		[
			fill	"#000000"
			Line
			[
				point
				[
					x	165.20000000000005
					y	435.0
				]
				point
				[
					x	165.20000000000005
					y	310.0
				]
				point
				[
					x	290.20000000000005
					y	310.0
				]
			]
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
					x	290.20000000000005
					y	310.0
				]
				point
				[
					x	240.0
					y	294.5741311999998
				]
				point
				[
					x	240.0
					y	260.6
				]
				point
				[
					x	340.6240000000001
					y	260.6
				]
				point
				[
					x	340.6240000000001
					y	293.5327641599999
				]
				point
				[
					x	290.20000000000005
					y	310.0
				]
			]
		]
		edgeAnchor
		[
			xSource	-0.5298970000000018
			ySource	-0.48205840000000677
			xTarget	0.4262169199999981
			yTarget	-0.5146011200000036
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
