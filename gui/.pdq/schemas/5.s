<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<schema name="5">
    <relations>
        <view name="region_nation">
            <attribute name="nation_key" type="java.lang.Integer"/>
            <attribute name="nation_name" type="java.lang.String"/>
            <attribute name="region_key" type="java.lang.Integer"/>
            <attribute name="region_name" type="java.lang.String"/>
        </view>
        <relation name="lineitem">
            <attribute name="l_orderkey" type="java.lang.Integer"/>
            <attribute name="l_partkey" type="java.lang.Integer"/>
            <attribute name="l_suppkey" type="java.lang.Integer"/>
            <attribute name="l_linenumber" type="java.lang.Integer"/>
            <attribute name="l_quantity" type="java.math.BigDecimal"/>
            <attribute name="l_extendedprice" type="java.math.BigDecimal"/>
            <attribute name="l_discount" type="java.math.BigDecimal"/>
            <attribute name="l_tax" type="java.math.BigDecimal"/>
            <attribute name="l_returnflag" type="java.lang.String"/>
            <attribute name="l_linestatus" type="java.lang.String"/>
            <attribute name="l_shipdate" type="java.sql.Date"/>
            <attribute name="l_commitdate" type="java.sql.Date"/>
            <attribute name="l_receiptdate" type="java.sql.Date"/>
            <attribute name="l_shipinstruct" type="java.lang.String"/>
            <attribute name="l_shipmode" type="java.lang.String"/>
            <attribute name="l_comment" type="java.lang.String"/>
            <access-method name="m17"/>
            <access-method name="m18" inputs="7"/>
            <access-method name="m19" inputs="7,8"/>
        </relation>
        <relation name="partsupp">
            <attribute name="ps_partkey" type="java.lang.Integer"/>
            <attribute name="ps_suppkey" type="java.lang.Integer"/>
            <attribute name="ps_availqty" type="java.lang.Integer"/>
            <attribute name="ps_supplycost" type="java.math.BigDecimal"/>
            <attribute name="ps_comment" type="java.lang.String"/>
            <access-method name="m8"/>
            <access-method name="m9" inputs="0,2"/>
        </relation>
        <relation name="nation">
            <attribute name="n_nationkey" type="java.lang.Integer"/>
            <attribute name="n_name" type="java.lang.String"/>
            <attribute name="n_regionkey" type="java.lang.Integer"/>
            <attribute name="n_comment" type="java.lang.String"/>
            <access-method name="m10"/>
            <access-method name="m11" inputs="1,2"/>
        </relation>
        <relation name="part">
            <attribute name="p_partkey" type="java.lang.Integer"/>
            <attribute name="p_name" type="java.lang.String"/>
            <attribute name="p_mfgr" type="java.lang.String"/>
            <attribute name="p_brand" type="java.lang.String"/>
            <attribute name="p_type" type="java.lang.String"/>
            <attribute name="p_size" type="java.lang.Integer"/>
            <attribute name="p_container" type="java.lang.String"/>
            <attribute name="p_retailprice" type="java.lang.String"/>
            <attribute name="p_comment" type="java.lang.String"/>
            <access-method name="m4"/>
            <access-method name="m5" inputs="0,1"/>
        </relation>
        <relation name="supplier">
            <attribute name="s_suppkey" type="java.lang.Integer"/>
            <attribute name="s_name" type="java.lang.String"/>
            <attribute name="s_address" type="java.lang.String"/>
            <attribute name="s_nationkey" type="java.lang.Integer"/>
            <attribute name="s_phone" type="java.lang.String"/>
            <attribute name="s_acctbal" type="java.math.BigDecimal"/>
            <attribute name="s_comment" type="java.lang.String"/>
            <access-method name="m6"/>
            <access-method name="m7" inputs="0,1"/>
        </relation>
        <relation name="orders">
            <attribute name="o_orderkey" type="java.lang.Integer"/>
            <attribute name="o_custkey" type="java.lang.Integer"/>
            <attribute name="o_orderstatus" type="java.lang.String"/>
            <attribute name="o_totalprice" type="java.math.BigDecimal"/>
            <attribute name="o_orderdate" type="java.sql.Date"/>
            <attribute name="o_orderpriority" type="java.lang.String"/>
            <attribute name="o_clerk" type="java.lang.String"/>
            <attribute name="o_shippriority" type="java.lang.Integer"/>
            <attribute name="o_comment" type="java.lang.String"/>
            <access-method name="m14"/>
            <access-method name="m15" inputs="3"/>
            <access-method name="m16" inputs="7"/>
        </relation>
        <relation name="region">
            <attribute name="r_regionkey" type="java.lang.Integer"/>
            <attribute name="r_name" type="java.lang.String"/>
            <attribute name="r_comment" type="java.lang.String"/>
            <access-method name="m12"/>
            <access-method name="m13" inputs="1"/>
        </relation>
        <relation name="customer">
            <attribute name="c_custkey" type="java.lang.Integer"/>
            <attribute name="c_name" type="java.lang.String"/>
            <attribute name="c_address" type="java.lang.String"/>
            <attribute name="c_nationkey" type="java.lang.Integer"/>
            <attribute name="c_phone" type="java.lang.String"/>
            <attribute name="c_acctbal" type="java.math.BigDecimal"/>
            <attribute name="c_mktsegment" type="java.lang.String"/>
            <attribute name="c_comment" type="java.lang.String"/>
            <access-method name="m1"/>
            <access-method name="m2" inputs="0"/>
            <access-method name="m3" inputs="3"/>
        </relation>
    </relations>
    <dependencies>
        <dependency type="TGD">
            <body>
                <atom name="lineitem">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                    <variable name="_7"/>
                    <variable name="_8"/>
                    <variable name="_9"/>
                    <variable name="_10"/>
                    <variable name="_11"/>
                    <variable name="_12"/>
                    <variable name="_13"/>
                    <variable name="_14"/>
                    <variable name="_15"/>
                </atom>
            </body>
            <head>
                <atom name="orders">
                    <variable name="_0"/>
                    <variable name="_17"/>
                    <variable name="_18"/>
                    <variable name="_19"/>
                    <variable name="_20"/>
                    <variable name="_21"/>
                    <variable name="_22"/>
                    <variable name="_23"/>
                    <variable name="_24"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="lineitem">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                    <variable name="_7"/>
                    <variable name="_8"/>
                    <variable name="_9"/>
                    <variable name="_10"/>
                    <variable name="_11"/>
                    <variable name="_12"/>
                    <variable name="_13"/>
                    <variable name="_14"/>
                    <variable name="_15"/>
                </atom>
            </body>
            <head>
                <atom name="partsupp">
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_18"/>
                    <variable name="_19"/>
                    <variable name="_20"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="partsupp">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                </atom>
            </body>
            <head>
                <atom name="supplier">
                    <variable name="_1"/>
                    <variable name="_6"/>
                    <variable name="_7"/>
                    <variable name="_8"/>
                    <variable name="_9"/>
                    <variable name="_10"/>
                    <variable name="_11"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="partsupp">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                </atom>
            </body>
            <head>
                <atom name="part">
                    <variable name="_0"/>
                    <variable name="_6"/>
                    <variable name="_7"/>
                    <variable name="_8"/>
                    <variable name="_9"/>
                    <variable name="_10"/>
                    <variable name="_11"/>
                    <variable name="_12"/>
                    <variable name="_13"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="nation">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                </atom>
            </body>
            <head>
                <atom name="region">
                    <variable name="_2"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="supplier">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                </atom>
            </body>
            <head>
                <atom name="nation">
                    <variable name="_3"/>
                    <variable name="_8"/>
                    <variable name="_9"/>
                    <variable name="_10"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="orders">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                    <variable name="_7"/>
                    <variable name="_8"/>
                </atom>
            </body>
            <head>
                <atom name="customer">
                    <variable name="_1"/>
                    <variable name="_10"/>
                    <variable name="_11"/>
                    <variable name="_12"/>
                    <variable name="_13"/>
                    <variable name="_14"/>
                    <variable name="_15"/>
                    <variable name="_16"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="customer">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                    <variable name="_4"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                    <variable name="_7"/>
                </atom>
            </body>
            <head>
                <atom name="nation">
                    <variable name="_3"/>
                    <variable name="_9"/>
                    <variable name="_10"/>
                    <variable name="_11"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="region_nation">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_5"/>
                </atom>
            </body>
            <head>
                <atom name="nation">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                </atom>
                <atom name="region">
                    <variable name="_2"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                </atom>
            </head>
        </dependency>
        <dependency type="TGD">
            <body>
                <atom name="nation">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_3"/>
                </atom>
                <atom name="region">
                    <variable name="_2"/>
                    <variable name="_5"/>
                    <variable name="_6"/>
                </atom>
            </body>
            <head>
                <atom name="region_nation">
                    <variable name="_0"/>
                    <variable name="_1"/>
                    <variable name="_2"/>
                    <variable name="_5"/>
                </atom>
            </head>
        </dependency>
    </dependencies>
</schema>
