/*
 * Copyright (C) 2008-2009 Holger Arndt, A. Naegele and M. Bundschus
 *
 * This file is part of the Universal Java Matrix Package (UJMP).
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * UJMP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * UJMP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with UJMP; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.ujmp.jscience;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javolution.util.FastTable;

import org.jscience.mathematics.vector.Float64Matrix;
import org.jscience.mathematics.vector.Float64Vector;
import org.ujmp.core.Matrix;
import org.ujmp.core.coordinates.Coordinates;
import org.ujmp.core.doublematrix.AbstractDenseDoubleMatrix2D;
import org.ujmp.core.exceptions.MatrixException;
import org.ujmp.core.interfaces.Wrapper;
import org.ujmp.core.util.ReflectionUtil;

public class JScienceDenseDoubleMatrix2D extends AbstractDenseDoubleMatrix2D
		implements Wrapper<Float64Matrix> {
	private static final long serialVersionUID = -7874694468839411484L;

	private transient Float64Matrix matrix = null;

	private Boolean transposed = false;

	private transient FastTable<Float64Vector> rows = null;

	public JScienceDenseDoubleMatrix2D(long... size) {
		if (Coordinates.product(size) != 0) {
			this.matrix = Float64Matrix
					.valueOf(new double[(int) size[ROW]][(int) size[COLUMN]]);
		}
	}

	public JScienceDenseDoubleMatrix2D(Float64Matrix matrix) {
		this.matrix = matrix;
	}

	public JScienceDenseDoubleMatrix2D(double[][] values) {
		this.matrix = Float64Matrix.valueOf(values);
	}

	public JScienceDenseDoubleMatrix2D(double[] values) {
		this.matrix = Float64Matrix.valueOf(Float64Vector.valueOf(values));
	}

	public JScienceDenseDoubleMatrix2D(Matrix matrix) throws MatrixException {
		this.matrix = Float64Matrix.valueOf(matrix.toDoubleArray());
	}

	public double getDouble(long row, long column) {
		return matrix.get((int) row, (int) column).doubleValue();
	}

	public double getDouble(int row, int column) {
		return matrix.get(row, column).doubleValue();
	}

	public long[] getSize() {
		return matrix == null ? Coordinates.ZERO2D : new long[] {
				matrix.getNumberOfRows(), matrix.getNumberOfColumns() };
	}

	public void setDouble(double value, long row, long column) {
		if (getTransposed()) {
			Float64Vector f = getRowsTable().get((int) column);
			double[] data = (double[]) ReflectionUtil.extractPrivateField(
					Float64Vector.class, f, "_values");
			data[(int) row] = value;
		} else {
			Float64Vector f = getRowsTable().get((int) row);
			double[] data = (double[]) ReflectionUtil.extractPrivateField(
					Float64Vector.class, f, "_values");
			data[(int) column] = value;
		}
	}

	private boolean getTransposed() {
		if (transposed == null) {
			transposed = (Boolean) ReflectionUtil.extractPrivateField(
					Float64Matrix.class, matrix, "_transposed");
		}
		return transposed;
	}

	@SuppressWarnings("unchecked")
	private FastTable<Float64Vector> getRowsTable() {
		if (rows == null) {
			rows = (FastTable<Float64Vector>) ReflectionUtil
					.extractPrivateField(Float64Matrix.class, matrix, "_rows");
		}
		return rows;
	}

	public void setDouble(double value, int row, int column) {
		if (getTransposed()) {
			Float64Vector f = getRowsTable().get(column);
			double[] data = (double[]) ReflectionUtil.extractPrivateField(
					Float64Vector.class, f, "_values");
			data[row] = value;
		} else {
			Float64Vector f = getRowsTable().get(row);
			double[] data = (double[]) ReflectionUtil.extractPrivateField(
					Float64Vector.class, f, "_values");
			data[column] = value;
		}
	}

	@Override
	public Matrix mtimes(Matrix that) {
		if (that instanceof JScienceDenseDoubleMatrix2D) {
			return new JScienceDenseDoubleMatrix2D(matrix
					.times(((JScienceDenseDoubleMatrix2D) that).matrix));
		} else {
			return super.mtimes(that);
		}
	}

	@Override
	public Matrix transpose() {
		return new JScienceDenseDoubleMatrix2D(matrix.transpose().copy());
	}

	public Float64Matrix getWrappedObject() {
		return matrix;
	}

	public void setWrappedObject(Float64Matrix object) {
		this.matrix = object;
	}

	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();
		double[][] values = (double[][]) s.readObject();
		matrix = Float64Matrix.valueOf(values);
	}

	private void writeObject(ObjectOutputStream s) throws IOException,
			MatrixException {
		s.defaultWriteObject();
		s.writeObject(this.toDoubleArray());
	}

}
