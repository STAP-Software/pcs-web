/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.PointListEncoder;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.session.model.FieldDescriptor;

@Entity
@Table(name = "ProcedureOutputValue")
@NamedQueries({
		@NamedQuery(name = "findOutputValuesForProcedure", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
				+ "where p.procedureId = :procedureId AND p.iteration is null ORDER BY f.displayOrder "),
		@NamedQuery(name = "findOutputValuesForProcedureIteration", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
				+ "where p.procedureId = :procedureId AND p.iteration = :iteration ORDER BY f.displayOrder ") })
public class ProcedureOutputValue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long procedureOutputValueId;

	Long procedureId;
	Integer iteration;

	@Column(nullable = false, length = 10000)
	private String data;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureOutputFieldId", nullable = false, updatable = false)
	ProcedureOutputField procedureOutputField;

	public Long getProcedureOutputValueId() {
		return procedureOutputValueId;
	}

	public void setProcedureOutputValueId(Long procedureOutputValueId) {
		this.procedureOutputValueId = procedureOutputValueId;
	}

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ProcedureOutputField getProcedureOutputField() {
		return procedureOutputField;
	}

	public void setProcedureOutputField(ProcedureOutputField procedureOutputField) {
		this.procedureOutputField = procedureOutputField;
	}

	public Integer getIteration() {
		return iteration;
	}

	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}

	public String getDataFormatted() {
		return reformatData(data, procedureOutputField);
	}

	private String reformatData(String value, FieldDescriptor fieldDescriptor) {

		if (value == null || value.trim().length() == 0 || value.equals("null")) {
			return "null";
		}
		
		String format = "{0,number," + fieldDescriptor.getDisplayFormat() + "}";

		
		if (procedureOutputField.isScalar()) {
			
			switch (fieldDescriptor.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intValue = new Integer(value);
				return MessageFormat.format(format, intValue);

			case Constant.DATA_TYPE_FLOAT:
				Float floatValue = new Float(value);
				return MessageFormat.format(format, floatValue);

			case Constant.DATA_TYPE_DOUBLE:
				Double doubleValue = new Double(value);
				return MessageFormat.format(format, doubleValue);

			case Constant.DATA_TYPE_BOOLEAN:
				return value;

			case Constant.DATA_TYPE_INT_POINT:
				Point intPointArray[] = PointListEncoder.decodeList(value).toArray(new Point[] {});
				Point intPointValue = intPointArray[0];
				return MessageFormat.format(format, intPointValue.x) + ", " + MessageFormat.format(format, intPointValue.y);
			
			case Constant.DATA_TYPE_FLOAT_POINT:
				FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
				FloatPoint floatPointValue = floatPointArray[0];
				return MessageFormat.format(format, floatPointValue.x) + ", " + MessageFormat.format(format, floatPointValue.y);
			}
			
		}
		
		StringBuffer buf = new StringBuffer();

		buf.append("<table>");

		if (fieldDescriptor.isOneDimensional()) {

			switch (fieldDescriptor.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intArray[] = IntegerListEncoder.decodeList(value).toArray(new Integer[] {});
				for (int i = 0; i < intArray.length; i++) {
					buf.append((i + 1) + "  " + intArray[i] + "\n");
				}
				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatArray[] = FloatListEncoder.decodeList(value).toArray(new Float[] {});
				for (int i = 0; i < floatArray.length; i++) {
					buf.append((i + 1) + "  " + floatArray[i] + "\n");
				}
				break;

			case Constant.DATA_TYPE_INT_POINT:
				Point pointArray[] = PointListEncoder.decodeList(value).toArray(new Point[] {});
				for (int i = 0; i < pointArray.length; i++) {
					buf.append((i + 1) + "      " + pointArray[i].x + "      " + pointArray[i].y + "\n");
				}
				break;

			case Constant.DATA_TYPE_FLOAT_POINT:
				FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
				for (int i = 0; i < floatPointArray.length; i++) {
					buf.append("<tr>");
					buf.append("<td>" + (i + 1) + "</td>");
					buf.append("<td style=\"text-align:right\">" + MessageFormat.format(format, floatPointArray[i].x) + "</td>");
					buf.append("<td>&nbsp;</td>");
					buf.append("<td style=\"text-align:right\">" + MessageFormat.format(format, floatPointArray[i].y) + "</td>");
					buf.append("<td>&nbsp;</td>");
					buf.append("</tr>");
				}
				break;
			}

		} else {

			// two dimensional (non-point type) arrays

			// assuming iteration over first index surrounds the second index iterator

			switch (fieldDescriptor.getDataType()) {
			case Constant.DATA_TYPE_INT:
				Integer intArray[] = IntegerListEncoder.decodeList(value).toArray(new Integer[] {});

				int int2dArray[][] = new int[fieldDescriptor.getDimension1()][fieldDescriptor.getDimension2()];

				// flat array now needs to be read into 2-d array
				int k = 0;
				for (int i = 0; i < fieldDescriptor.getDimension1(); i++) {
					for (int j = 0; j < fieldDescriptor.getDimension2(); j++) {

						int2dArray[i][j] = intArray[k++].intValue();
					}
				}

				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatArray[] = FloatListEncoder.decodeList(value).toArray(new Float[] {});

				// flat array now needs to be read into 2-d array
				int fk = 0;
				for (int fi = 0; fi < fieldDescriptor.getDimension1(); fi++) {
					buf.append("<tr>");
					buf.append("<td>" + (fi + 1) + "</td>");

					for (int fj = 0; fj < fieldDescriptor.getDimension2(); fj++) {
						
						String sresult =  MessageFormat.format(format, floatArray[fk++].floatValue());
						if (sresult.contains("E") && !sresult.contains("E-")) { //don't blast a negative sign
							sresult = sresult.replace("E", "E+");
						}
						buf.append("<td style=\"text-align:right\">" + sresult  + "</td>");
						buf.append("<td>&nbsp;</td>");
					}
					buf.append("</tr>");
				}

				break;

			}
		}
		buf.append("</table>");
		return buf.toString();

	}

}
